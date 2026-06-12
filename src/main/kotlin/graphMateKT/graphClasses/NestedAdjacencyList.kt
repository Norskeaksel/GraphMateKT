package graphMateKT.graphClasses

import graphMateKT.Edge
import graphMateKT.Edges

internal class NestedAdjacencyList(private val adjacencyList: MutableList<Edges>) : AdjacencyList {

    override fun nodes() = IntArray(adjacencyList.size) { it }
    override fun neighbours(node: Int): IntArray = adjacencyList[node].let { neighbours ->
        IntArray(neighbours.size) { i -> neighbours[i].second }
    }

    override fun edges(node: Int): Edges = adjacencyList[node]

    override fun forEachNeighbour(node: Int, action: (Int) -> Unit) {
        adjacencyList[node].forEach { (_, v) ->
            action(v)
        }
    }

    override fun forEachEdge(node: Int, action: (Double, Int) -> Unit) {
        adjacencyList[node].forEach { (w, v) ->
            action(w, v)
        }
    }

    override fun deepCopy() = NestedAdjacencyList(adjacencyList.map { it.toMutableList() }.toMutableList())
    override val size get() = adjacencyList.size
    override fun reversed(): AdjacencyList {
        val reversedAdjacencyList = MutableList<Edges>(adjacencyList.size) { mutableListOf() }
        adjacencyList.forEachIndexed { u, edges ->
            edges.forEach { (w, v) ->
                reversedAdjacencyList[v].add(Edge(w, u))
            }
        }
        return NestedAdjacencyList(reversedAdjacencyList)
    }
}
