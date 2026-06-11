package graphMateKT.graphAlgorithms

import graphMateKT.graphClasses.AdjacencyList


internal class BFS(private val graph: AdjacencyList) {
    fun bfs(
        startIds: List<Int>,
        targetId: Int = -1,
        previousSearchResult: GraphSearchResults? = null,
    ): GraphSearchResults {
        val r = previousSearchResult ?: GraphSearchResults(graph.size)
        r.currentVisited.clear()
        val queue = ArrayDeque<Int>()
        startIds.forEach {
            queue.add(it)
            r.unweightedDistances[it] = 0
        }
        while (queue.isNotEmpty() && !r.foundTarget) {
            val currentId = queue.removeFirst()
            if (r.visited[currentId])
                continue
            r.visited[currentId] = true
            r.currentVisited.add(currentId)

            val currentDistance = r.unweightedDistances[currentId]
            graph.forEachNeighbour(currentId) { v ->
                val newDistance = currentDistance + 1
                if ((!r.visited[v] && newDistance < r.unweightedDistances[v]) || v == targetId) {
                    r.parents[v] = currentId
                    r.depth = newDistance.coerceAtLeast(r.depth)
                    r.unweightedDistances[v] = newDistance
                    if (v == targetId) {
                        r.foundTarget = true
                    }
                    queue.add(v)
                }
            }
        }
        r.processedOrder = r.currentVisited
        return r
    }
}
