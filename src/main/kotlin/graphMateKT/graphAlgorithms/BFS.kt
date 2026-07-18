package graphMateKT.graphAlgorithms

import graphMateKT.graphClasses.AdjacencyList


internal class BFS(private val graph: AdjacencyList) {
    fun bfs(
        startIds: List<Int>,
        targetIds: List<Int> = emptyList(),
        previousSearchResult: GraphSearchResults? = null,
    ): GraphSearchResults {
        val r = previousSearchResult ?: GraphSearchResults(graph.size)
        val targets = BooleanArray(graph.size)
        targetIds.forEach { targetId ->
            targets[targetId] = true
        }
        r.currentVisited.clear()
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
            graph.forEachNeighbour(currentId) { v ->
                val newDistance = currentDistance + 1
                if ((!r.visited[v] && newDistance < r.distances[v]) || targets[v]) {
                    r.parents[v] = currentId
                    r.depth = newDistance.toInt().coerceAtLeast(r.depth)
                    r.distances[v] = newDistance
                    if (targets[v]) {
                        r.currentVisited.add(v)
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
