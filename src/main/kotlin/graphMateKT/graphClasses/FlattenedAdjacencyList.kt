package graphMateKT.graphClasses

import graphMateKT.Edge
import graphMateKT.Edges

internal class FlattenedAdjacencyList(
    private val flattenedAdjacencyList: IntArray,
    private val starts: IntArray,
    private val ends: IntArray,
    private val flattenedWeights: DoubleArray,
) : AdjacencyList {

    override fun nodes() = IntArray(size) { it }
    override fun getNeighbours(node: Int): IntArray {
        val start = starts[node]
        val end = ends[node]
        return flattenedAdjacencyList.sliceArray(start until end)
    }

    override fun getEdges(node: Int): Edges {
        val start = starts[node]
        val end = ends[node]
        return MutableList(end - start) { i -> Edge(flattenedWeights[start + i], flattenedAdjacencyList[start + i]) }
    }

    override fun deepCopy(): AdjacencyList = FlattenedAdjacencyList(
        flattenedAdjacencyList.copyOf(),
        starts.copyOf(),
        ends.copyOf(),
        flattenedWeights.copyOf(),
    )

    override val size get() = starts.size
    override fun reversed(): AdjacencyList = FlattenedAdjacencyList(
        flattenedAdjacencyList.reversedArray(),
        starts.reversedArray(),
        ends.reversedArray(),
        flattenedWeights.reversedArray()
    )
}
