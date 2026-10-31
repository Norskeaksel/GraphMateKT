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
            if (r.visited[currentId] != 0)
                continue
            r.visited[currentId] = 1
            if(storeVisitationOrder)
                r.currentVisited.add(currentId)

            val currentDistance = r.distances[currentId]
            if (currentDistance >= maxDepth) {
                continue
            }
            graph.forEachNeighbour(currentId) { v ->
                val newDistance = currentDistance + 1
                try {
                    if ((r.visited[v] != 0 && newDistance < r.distances[v]) || v == targetId) {
                        if(storeParents)
                            r.parents[v] = currentId
                        depth = newDistance.toInt().coerceAtLeast(depth)
                        r.distances[v] = newDistance
                        if (v == targetId) {
                            if(storeVisitationOrder)
                                r.currentVisited.add(v)
                            r.visited[v] = 1
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
}
