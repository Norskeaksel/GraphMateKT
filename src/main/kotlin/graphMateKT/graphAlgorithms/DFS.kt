package graphMateKT.graphAlgorithms

import graphMateKT.GraphSearchResults
import graphMateKT.IntArrayList
import graphMateKT.IntComponents
import graphMateKT.InvalidEdgeException
import graphMateKT.graphClasses.AdjacencyList


internal class DFS(private val graph: AdjacencyList) {
    private var r = GraphSearchResults(graph.size)
    fun dfs(
        start: Int,
        initialSearchResults: GraphSearchResults? = null,
    ): GraphSearchResults {
        r = initialSearchResults ?: GraphSearchResults(graph.size)
        r.currentVisited = IntArrayList()
        var maxDepth = r.depth ?: 0
        fun visit(id: Int, depth: Int) {
            if (r.visited[id]) return
            r.visited[id] = true
            r.currentVisited.add(id)
            r.distances[id] = depth.toDouble()
            maxDepth = depth.coerceAtLeast(maxDepth)
            graph.forEachNeighbour(id) { v ->
                try {
                    if (!r.visited[v]) {
                        r.parents[v] = id
                        visit(v, depth + 1)
                    }
                } catch (_: ArrayIndexOutOfBoundsException) {
                    throw InvalidEdgeException(id, v)
                }
            }
            r.processedOrder.add(id)
        }

        visit(start, 0)
        r.depth = maxDepth
        return r
    }

    fun dfsSparse(
        start: Int,
        initialSearchResults: GraphSearchResults? = null,
    ): GraphSearchResults {
        r = initialSearchResults ?: GraphSearchResults(graph.size)
        r.currentVisited = IntArrayList()
        var maxDepth = r.depth ?: 0
        fun visit(id: Int, depth: Int) {
            if (id in r.visitedSparse) return
            r.visitedSparse.add(id)
            r.currentVisited.add(id)
            r.distancesSparse[id] = depth.toDouble()
            maxDepth = depth.coerceAtLeast(maxDepth)
            graph.forEachNeighbour(id) { v ->
                try {
                    if (v !in r.visitedSparse) {
                        r.parentsSparse[v] = id
                        visit(v, depth + 1)
                    }
                } catch (_: ArrayIndexOutOfBoundsException) {
                    throw InvalidEdgeException(id, v)
                }
            }
            r.processedOrder.add(id)
        }

        visit(start, 0)
        r.depth = maxDepth
        return r
    }

    fun stronglyConnectedComponents(deleted: BooleanArray = BooleanArray(graph.size)): IntComponents {
        val topologicalOrder = DFS(graph.reversed()).topologicalSort(deleted).reversed()
        val stronglyConnectedComponents = topologicalOrder.mapNotNull { id ->
            if (r.visited[id]) null
            else {
                dfs(id, r)
                r.currentVisited
            }
        }
        return stronglyConnectedComponents.map { it.asList() }
    }

    fun topologicalSort(deleted: BooleanArray = BooleanArray(graph.size)): List<Int> {
        for (i in 0 until graph.size) {
            if (deleted[i]) continue
            dfs(i, r)
        }
        return r.processedOrder.asList()
    }
}
