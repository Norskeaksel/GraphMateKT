package graphMateKT.graphAlgorithms

import graphMateKT.GraphSearchResults
import graphMateKT.InvalidEdgeException
import graphMateKT.graphClasses.AdjacencyList


internal class BFS(
    private val graph: AdjacencyList,
    private val targetId: Int,
    private val maxDistance: Double,
    private val bidirectional: Boolean,
) {
    private var depth: Int = 0
    private var foundTarget: Boolean = false

    fun bfs(
        startIds: List<Int>,
        previousSearchResult: GraphSearchResults? = null,
    ): GraphSearchResults {
        val r = previousSearchResult ?: GraphSearchResults(graph.size)
        r.currentVisited.clear()
        r.depth?.let { depth = it }
        val queue = ArrayDeque<Int>()
        startIds.forEach {
            queue.add(it)
            r.distances[it] = 0.0
        }
        val targetQueue = ArrayDeque<Int>()
        if (bidirectional) {
            targetQueue.add(targetId)
        }
        while ((queue.isNotEmpty() || targetQueue.isNotEmpty()) && !foundTarget) {
            val u: Int
            var useNormalQueue = true
            if (bidirectional && targetQueue.size <= queue.size) {
                useNormalQueue = false
                u = targetQueue.removeFirst()
                if (r.targetVisited[u])
                    continue
                r.targetVisited[u] = true
            } else {
                u = queue.removeFirst()
                if (r.visited[u])
                    continue
                r.visited[u] = true
            }
            r.currentVisited.add(u)

            val currentDistance = r.distances[u]
            if (currentDistance >= maxDistance) {
                continue
            }
            graph.forEachNeighbour(u) { v ->
                val newDistance = currentDistance + 1
                try {
                    if (useNormalQueue) {
                        visitIfNeeded(u, v, newDistance, r.visited, r.distances, r.parents, queue)
                    } else {
                        visitIfNeeded(u, v, newDistance, r.targetVisited, r.distances, r.parents, targetQueue)
                    }
                    if (foundTarget) {
                        r.currentVisited.add(v)
                    }
                } catch (_: ArrayIndexOutOfBoundsException) {
                    throw InvalidEdgeException(u, v)
                }
            }
        }
        r.depth = depth
        r.processedOrder = r.currentVisited
        r.foundTarget = foundTarget
        return r
    }

    private fun visitIfNeeded(
        u: Int,
        v: Int,
        newDistance: Double,
        visited: BooleanArray,
        distances: DoubleArray,
        parents: IntArray,
        queue: ArrayDeque<Int>,
    ) {
        if ((!visited[v] && newDistance < distances[v]) || v == targetId) {
            parents[v] = u
            depth = newDistance.toInt().coerceAtLeast(depth)
            distances[v] = newDistance
            if (v == targetId) {
                visited[v] = true
                foundTarget = true
            }
            queue.add(v)
        }
    }

    fun bfsSparse(
        startIds: List<Int>,
        previousSearchResult: GraphSearchResults? = null,
    ): GraphSearchResults {
        val r = previousSearchResult ?: GraphSearchResults(graph.size)
        r.currentVisited.clear()
        r.depth?.let { depth = it }
        val queue = ArrayDeque<Int>()
        startIds.forEach {
            queue.add(it)
            r.distancesSparse[it] = 0.0
        }
        val targetQueue = ArrayDeque<Int>()
        if (bidirectional) {
            targetQueue.add(targetId)
        }
        while ((queue.isNotEmpty() || targetQueue.isNotEmpty()) && !foundTarget) {
            val u: Int
            var useNormalQueue = true
            if (bidirectional && targetQueue.size <= queue.size) {
                useNormalQueue = false
                u = targetQueue.removeFirst()
                if (u in r.targetVisitedSparse) {
                    continue
                }
                r.targetVisitedSparse.add(u)
            } else {
                u = queue.removeFirst()
                if (u in r.visitedSparse)
                    continue
                r.visitedSparse.add(u)
            }
            r.currentVisited.add(u)
            val currentDistance = r.distancesSparse[u]!!
            if (currentDistance >= maxDistance) {
                continue
            }
            graph.forEachNeighbour(u) { v ->
                val newDistance = currentDistance + 1
                // @formatter:off
                if(useNormalQueue)
                    visitSparseIfNeeded(u, v, newDistance, r.visitedSparse, r.distancesSparse, r.parentsSparse, queue)
                else
                    visitSparseIfNeeded(u, v, newDistance, r.targetVisitedSparse, r.distancesSparse, r.parentsSparse, targetQueue)
                // @formatter:on
                if (foundTarget) {
                    r.currentVisited.add(v)
                }
            }
        }
        r.depth = depth
        r.processedOrder = r.currentVisited
        r.foundTarget = foundTarget
        return r
    }


    private fun visitSparseIfNeeded(
        u: Int,
        v: Int,
        newDistance: Double,
        visited: MutableSet<Int>,
        distances: MutableMap<Int, Double>,
        parents: MutableMap<Int, Int>,
        queue: ArrayDeque<Int>,
    ) {
        if ((v !in visited && newDistance < (distances[v] ?: Double.POSITIVE_INFINITY)) || v == targetId) {
            parents[v] = u
            depth = newDistance.toInt().coerceAtLeast(depth)
            distances[v] = newDistance
            if (v == targetId) {
                visited.add(v)
                foundTarget = true
            }
            queue.add(v)
        }
    }
}
