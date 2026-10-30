package graphMateKT.graphAlgorithms

import graphMateKT.GraphSearchResults
import graphMateKT.InvalidEdgeException
import graphMateKT.graphClasses.AdjacencyList


internal class BFS(private val graph: AdjacencyList) {
    fun bfs(
        startIds: List<Int>,
        targetId: Int = -1,
        previousSearchResult: GraphSearchResults? = null,
        maxDepth: Double = Double.POSITIVE_INFINITY,
        storeParents: Boolean = true,
        storeVisitationOrder: Boolean = true,
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
            if(storeVisitationOrder)
                r.currentVisited.add(currentId)

            val currentDistance = r.distances[currentId]
            if (currentDistance >= maxDepth) {
                continue
            }
            graph.forEachNeighbour(currentId) { v ->
                val newDistance = currentDistance + 1
                try {
                    if ((!r.visited[v] && newDistance < r.distances[v]) || v == targetId) {
                        if(storeParents)
                            r.parents[v] = currentId
                        depth = newDistance.toInt().coerceAtLeast(depth)
                        r.distances[v] = newDistance
                        if (v == targetId) {
                            if(storeVisitationOrder)
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
        maxDepth: Double = Double.POSITIVE_INFINITY,
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
            if (currentDistance >= maxDepth) {
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
}
