package graphMateKT.graphClasses

import graphMateKT.Edge
import graphMateKT.Edges

interface AdjacencyList {

    fun nodes(): IntArray
    fun getNeighbours(node: Int): IntArray
    fun getEdges(node: Int): Edges
    fun deepCopy(): AdjacencyList
    val size: Int
    fun reversed(): AdjacencyList
}

class NestedAdjacencyList(private val adjacencyList: MutableList<Edges>) : AdjacencyList {

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

class FlattenedAdjacencyList(
    private val flattenedAdjacencyList: IntArray,
    private val starts: IntArray,
    private val ends: IntArray,
    private val weights: DoubleArray,
) : AdjacencyList {

    override fun nodes() = IntArray(starts.size) { it }
    override fun getNeighbours(node: Int): IntArray {
        val start = starts[node]
        val end = ends[node]
        return flattenedAdjacencyList.sliceArray(start until end)
    }

    override fun getEdges(node: Int): Edges {
        val start = starts[node]
        val end = ends[node]
        return MutableList(end - start) { i -> Edge(weights[start + i], flattenedAdjacencyList[start + i])}
    }

    override fun deepCopy(): AdjacencyList = FlattenedAdjacencyList(
        flattenedAdjacencyList.copyOf(),
        starts.copyOf(),
        ends.copyOf(),
        weights.copyOf(),
    )

    override val size get() = starts.size
    override fun reversed(): AdjacencyList {
        TODO("Not yet implemented")
    }
}
