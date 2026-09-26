package graphMateKT.graphClasses

import graphMateKT.UnboxedEdges

internal class FlattenedAdjacencyList(val nrOfNodes: Int, val edges: UnboxedEdges) : AdjacencyList {
    val nrOfEdges = edges.size
    val starts: IntArray = IntArray(nrOfNodes)
    val ends: IntArray = IntArray(nrOfNodes)
    val flattenedNeighbours = IntArray(nrOfEdges)
    val flattenedWeights = DoubleArray(nrOfEdges)

    init {
        val nrOfEdgesFrom = IntArray(nrOfNodes)
        edges.from.intArray().forEach { nrOfEdgesFrom[it]++ }
        var sum = 0
        repeat(nrOfNodes) { i ->
            starts[i] = sum
            sum += nrOfEdgesFrom[i]
            ends[i] = sum
        }
        repeat(nrOfEdges) { i ->
            edges.run {
                val u = from[i]
                val v = to[i]
                val offset = --nrOfEdgesFrom[u]
                val idx = starts[u] + offset
                flattenedNeighbours[idx] = v
                flattenedWeights[idx] = weights[i]
            }
        }
    }

    override fun nodes() = IntArray(size) { it }
    override fun neighbours(node: Int): IntArray {
        val start = starts[node]
        val end = ends[node]
        return flattenedNeighbours.copyOfRange(start, end)
    }

    override fun weights(node: Int): DoubleArray {
        val start = starts[node]
        val end = ends[node]
        return flattenedWeights.copyOfRange(start, end)
    }

    override fun forEachNeighbour(node: Int, action: (Int) -> Unit) {
        val start = starts[node]
        val end = ends[node]
        for (i in start until end) {
            action(flattenedNeighbours[i])
        }
    }

    override fun forEachEdge(node: Int, action: (Double, Int) -> Unit) {
        val start = starts[node]
        val end = ends[node]
        for (i in start until end) {
            action(flattenedWeights[i], flattenedNeighbours[i])
        }
    }

    override fun deepCopy(): AdjacencyList = FlattenedAdjacencyList(nrOfNodes, edges.deepCopy())

    override val size get() = nrOfNodes
    override fun reversed(): AdjacencyList = FlattenedAdjacencyList(nrOfNodes, edges.reversed())
}
