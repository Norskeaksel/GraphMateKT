package graphMateKT.graphAlgorithms

import graphMateKT.IntComponents
import graphMateKT.UnweightedAdjacencyList


internal class DFS(private val graph: UnweightedAdjacencyList) {
    private var r = GraphSearchResults(graph.size)

    fun dfsDeep(
        start: Int,
        initialSearchResults: GraphSearchResults? = null,
    ): GraphSearchResults {
        r = initialSearchResults ?: GraphSearchResults(graph.size)
        r.currentVisited = mutableListOf()
        var currentDepth = 0
        DeepRecursiveFunction<Int, Unit> { id ->
            if (r.visited[id]) return@DeepRecursiveFunction
            r.visited[id] = true
            r.currentVisited.add(id)
            r.depth = (++currentDepth).coerceAtLeast(r.depth)
            graph[id].forEach { v ->
                r.parents[v] = id
                this.callRecursive(v)
            }
            r.processedOrder.add(id)
            currentDepth-- //Done with this node. Backtracking to previous one.
        }.invoke(start)
        return r
    }

    fun dfs(
        start: Int,
        initialSearchResults: GraphSearchResults? = null,
    ): GraphSearchResults {
        try {
            r = initialSearchResults ?: GraphSearchResults(graph.size)
            r.currentVisited = mutableListOf()
            var currentDepth = 0

            fun visit(id: Int) {
                if (r.visited[id]) return
                r.visited[id] = true
                r.currentVisited.add(id)
                r.depth = (++currentDepth).coerceAtLeast(r.depth)
                graph[id].forEach { v ->
                    r.parents[v] = id
                    visit(v)
                }
                r.processedOrder.add(id)
                currentDepth-- // Done with this node. Backtracking to previous one.
            }

            visit(start)
            return r
        }
        catch (e: StackOverflowError) {
            System.err.println("Normal dfc got a ${e.cause} trying again with deepRecursve Function")
            return dfsDeep(start, initialSearchResults)
        }
    }

    fun stronglyConnectedComponents(deleted: BooleanArray = BooleanArray(graph.size)): IntComponents {
        val reversedGraph: UnweightedAdjacencyList =
            MutableList<MutableList<Int>>(graph.size) { mutableListOf() }.apply {
                graph.forEachIndexed { u, neighbors ->
                    neighbors.forEach { v ->
                        this[v].add(u)
                    }
                }
            }
        val topologicalOrder = DFS(reversedGraph).topologicalSort(deleted).reversed()
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
