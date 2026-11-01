package graphMateKT.graphAlgorithms

import graphMateKT.GraphSearchResults
import graphMateKT.InvalidEdgeException
import graphMateKT.graphClasses.AdjacencyList
import kotlin.collections.set
import kotlin.math.ceil


internal class BFS(private val graph: AdjacencyList) {
    fun bfs(
        startIds: List<Int>,
        targetId: Int = -1,
        previousSearchResult: GraphSearchResults? = null,
        maxDistance: Double = Double.POSITIVE_INFINITY,
    ): GraphSearchResults {
        val r = previousSearchResult ?: GraphSearchResults(graph.size)
        r.currentVisited.clear()
        var depth = r.depth ?: 0
        val queue = ArrayDeque<Int>()
        startIds.forEach {
            queue.add(it)
            r.distances[it] = 0.0
        }
        while (queue.isNotEmpty() && !r.foundTarget) {
            val currentId = queue.removeFirst()
            if (r.visited[currentId])
                continue
            r.visited[currentId] = true
            r.currentVisited.add(currentId)

            val currentDistance = r.distances[currentId]
            if (currentDistance >= maxDistance) {
                continue
            }
            graph.forEachNeighbour(currentId) { v ->
                val newDistance = currentDistance + 1
                try {
                    if ((!r.visited[v] && newDistance < r.distances[v]) || v == targetId) {
                        r.parents[v] = currentId
                        depth = newDistance.toInt().coerceAtLeast(depth)
                        r.distances[v] = newDistance
                        if (v == targetId) {
                            r.currentVisited.add(v)
                            r.visited[v] = true
                            r.foundTarget = true
                        }
                        queue.add(v)
                    }
                } catch (_: ArrayIndexOutOfBoundsException) {
                    throw InvalidEdgeException(currentId, v)
                }
            }
        }
        r.depth = depth
        r.processedOrder = r.currentVisited
        return r
    }

    fun bfsSparse(
        startIds: List<Int>,
        targetId: Int = -1,
        previousSearchResult: GraphSearchResults? = null,
        maxDistance: Double = Double.POSITIVE_INFINITY,
    ): GraphSearchResults {
        val r = previousSearchResult ?: GraphSearchResults(graph.size)
        r.currentVisited.clear()
        var depth = r.depth ?: 0
        val queue = ArrayDeque<Int>()
        startIds.forEach {
            queue.add(it)
            r.distancesSparse[it] = 0.0
        }
        while (queue.isNotEmpty() && !r.foundTarget) {
            val currentId = queue.removeFirst()
            if (currentId in r.visitedSparse)
                continue
            r.currentVisited.add(currentId)
            r.visitedSparse.add(currentId)

            val currentDistance = r.distancesSparse[currentId]!!
            if (currentDistance >= maxDistance) {
                continue
            }
            graph.forEachNeighbour(currentId) { v ->
                val newDistance = currentDistance + 1
                if (v == targetId ||
                    (v !in r.distancesSparse && newDistance < (r.distancesSparse[v] ?: Double.POSITIVE_INFINITY))
                ) {
                        r.parentsSparse[v] = currentId
                    depth = newDistance.toInt().coerceAtLeast(depth)
                    r.distancesSparse[v] = newDistance
                    if (v == targetId) {
                            r.currentVisited.add(v)
                        r.visitedSparse.add(v)
                        r.foundTarget = true
                    }
                    queue.add(v)
                }
            }
        }
        r.depth = depth
        r.processedOrder = r.currentVisited
        return r
    }

    fun bfsBidirectional(startNode: Int, target: Int, maxDistanceBetweenStartAndTarget: Double): Double {
        val maxDistance = ceil(maxDistanceBetweenStartAndTarget / 2)
        var currentMaxDistance = 1.0
        var startNodeResults = GraphSearchResults(0)
        var targetResults = GraphSearchResults(0)
        while (currentMaxDistance <= maxDistance) {
            targetResults = bfsSparse(listOf(target), maxDistance = currentMaxDistance)
            startNodeResults = bfsSparse(listOf(startNode), maxDistance = currentMaxDistance)
            val intersection = startNodeResults.visitedSparse.intersect(targetResults.visitedSparse)
            if (intersection.isNotEmpty()) {
                val targetDistance =
                    intersection.minOf { startNodeResults.distancesSparse[it]!! + targetResults.distancesSparse[it]!! }
                return targetDistance
            }
            currentMaxDistance++
        }
        return Double.POSITIVE_INFINITY
    }
}

/*    fun bfsBidirectional(startNode: Int, target: Int, maxDistanceBetweenStartAndTarget: Double): Double {
        val maxDistance = ceil(maxDistanceBetweenStartAndTarget / 2)
        var currentMaxDistance = 1.0
        while (currentMaxDistance <= maxDistance) {
            // @formatter:off
            val (targetVisited, targetDistances) = bfsSparse(listOf(target), maxDistance = currentMaxDistance).run { visitedSparse to distancesSparse }
            val (startVisited, startDistances) = bfsSparse(listOf(startNode), maxDistance = currentMaxDistance).run { visitedSparse to distancesSparse }
            // @formatter:on
            val intersection = startVisited.intersect(targetVisited)
            if (intersection.isNotEmpty()) {
                val targetDistance = intersection.minOf { startDistances[it]!! + targetDistances[it]!! }
                return targetDistance
            }
            currentMaxDistance++
        }
        return Double.POSITIVE_INFINITY
    */
