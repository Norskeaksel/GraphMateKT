package graphMateKT.graphClasses

import graphMateKT.DoubleArrayList
import graphMateKT.IntArrayList
import graphMateKT.UnboxedEdges

internal class AdjacencyListNested(private val nrOfNodes: Int, private val edges: UnboxedEdges) : AdjacencyList {
    private val nodes = IntArray(nrOfNodes) { it }
    private val neighbours = Array(nrOfNodes) { IntArrayList() }
    private val weights = Array(nrOfNodes) { DoubleArrayList() }

    init {
        repeat(edges.size) {
            val u = edges.from[it]
            val v = edges.to[it]
            val w = edges.weights[it]
            neighbours[u].add(v)
            weights[u].add(w)
        }
    }

    override val isDynamic = false
    override fun nodes() = nodes
    override fun edges() = edges.boxedEdges()
    override fun neighbours(node: Int): IntArray = neighbours[node].intArray()
    override fun weights(node: Int) = weights[node].doubleArray()

    override fun forEachNeighbour(node: Int, action: (Int) -> Unit) {
        neighbours[node].intArray().forEach { v ->
            action(v)
        }
    }

    override fun forEachEdge(node: Int, action: (Double, Int) -> Unit) {
        val neighbours = neighbours[node].intArray()
        val weights = weights[node].doubleArray()
        neighbours.indices.forEach {
            action(weights[it], neighbours[it])
        }
    }

    override fun deepCopy() = AdjacencyListNested(nrOfNodes, edges.deepCopy())
    override val size get() = nrOfNodes

    override fun reversed() = AdjacencyListNested(nrOfNodes, edges.reversed())
}
