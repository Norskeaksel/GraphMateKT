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
    override fun neighbours(node: Int): IntArray {
        val start = starts[node]
        val end = ends[node]
        return IntArray(end - start) { flattenedAdjacencyList[start + it] }
    }

    override fun edges(node: Int): Edges {
        val start = starts[node]
        val end = ends[node]
        return MutableList(end - start) { Edge(flattenedWeights[start + it], flattenedAdjacencyList[start + it]) }
    }

    override fun forEachNeighbour(node: Int, action: (Int) -> Unit) {
        val start = starts[node]
        val end = ends[node]
        for (i in start until end) {
            action(flattenedAdjacencyList[i])
        }
    }

    override fun forEachEdge(node: Int, action: (Double, Int) -> Unit) {
        val start = starts[node]
        val end = ends[node]
        for (i in start until end) {
            action(flattenedWeights[i], flattenedAdjacencyList[i])
        }
    }

    override fun deepCopy(): AdjacencyList = FlattenedAdjacencyList(
        flattenedAdjacencyList.copyOf(),
        starts.copyOf(),
        ends.copyOf(),
        flattenedWeights.copyOf(),
    )

    override val size get() = starts.size
    override fun reversed(): AdjacencyList {
        val revStarts = IntArray(size)
        val revEnds = IntArray(size)
        flattenedAdjacencyList.forEach { revEnds[it]++ }
        for (i in 1 until size) revStarts[i] = revStarts[i - 1] + revEnds[i - 1]
        for (i in 0 until size) revEnds[i] += revStarts[i]

        val revAdj = IntArray(flattenedAdjacencyList.size)
        val revW = DoubleArray(flattenedWeights.size)
        val next = revStarts.copyOf()
        repeat(size) { u ->
            for (idx in starts[u] until ends[u]) {
                val v = flattenedAdjacencyList[idx]
                val at = next[v]++
                revAdj[at] = u
                revW[at] = flattenedWeights[idx]
            }
        }
        return FlattenedAdjacencyList(revAdj, revStarts, revEnds, revW)
    }
}
