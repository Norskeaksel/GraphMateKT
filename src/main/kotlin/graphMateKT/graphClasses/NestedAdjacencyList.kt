package graphMateKT.graphClasses

import graphMateKT.Edge
import graphMateKT.Edges

internal class NestedAdjacencyList(private val adjacencyList: MutableList<Edges>) : AdjacencyList {

    override fun nodes() = IntArray(adjacencyList.size) { it }

    override fun getNeighbours(node: Int): IntArray = adjacencyList[node].let { neighbours ->
        IntArray(neighbours.size) { i -> neighbours[i].second }
    }

    override fun getEdges(node: Int) = adjacencyList[node]
    override fun deepCopy() = NestedAdjacencyList(adjacencyList.map { it.toMutableList() }.toMutableList())
    override val size get() = adjacencyList.size
    override fun reversed() = NestedAdjacencyList(MutableList<Edges>(size) { mutableListOf() }.apply {
        nodes().forEach { u ->
            getEdges(u).forEach { v ->
                this[v.second].add(Edge(v.first, u))
            }
        }
    }
    )
}
