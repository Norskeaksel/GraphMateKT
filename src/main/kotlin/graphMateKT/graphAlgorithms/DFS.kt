package graphMateKT.graphAlgorithms

import graphMateKT.IntComponents
import graphMateKT.graphClasses.AdjacencyList


internal class DFS(private val graph: AdjacencyList) {
    private var r = GraphSearchResults(graph.size)

    fun dfs(
        start: Int,
        initialSearchResults: GraphSearchResults? = null,
    ): GraphSearchResults {
        r = initialSearchResults ?: GraphSearchResults(graph.size)
        r.currentVisited = mutableListOf()
        fun visit(id: Int, depth: Int) {
            if (r.visited[id]) return
            r.visited[id] = true
            r.currentVisited.add(id)
            r.depth = (depth).coerceAtLeast(r.depth)
            graph.forEachNeighbour(id) { v ->
                r.parents[v] = id
                visit(v, depth + 1)
            }
            r.processedOrder.add(id)
        }

        visit(start, 1)
        return r
    }

    fun stronglyConnectedComponents(deleted: BooleanArray = BooleanArray(graph.size)): IntComponents {
        val topologicalOrder = DFS(graph.reversed()).topologicalSort(deleted).reversed()
        val stronglyConnectedComponents: IntComponents = topologicalOrder.mapNotNull { id ->
            if (r.visited[id]) null
            else {
                dfs(id, r)
                r.currentVisited
            }
        }
        return stronglyConnectedComponents
    }

    fun topologicalSort(deleted: BooleanArray = BooleanArray(graph.size)): List<Int> {
        for (i in 0 until graph.size) {
            if (deleted[i]) continue
            dfs(i, r)
        }
        return r.processedOrder//.reversed() //Reversed depending on the order
    }
}
