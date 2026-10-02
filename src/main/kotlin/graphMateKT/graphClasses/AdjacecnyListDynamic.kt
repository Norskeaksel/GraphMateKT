package graphMateKT.graphClasses

import graphMateKT.IntArrayList

internal class AdjacecnyListDynamic(
    override val size: Int,
    private val forEachNeighbourOf: (node: Int, action: (Int) -> Unit) -> Unit,
) : AdjacencyList {
    private fun unsupported(): Nothing {
        val callerName = Thread.currentThread().stackTrace
            .drop(1) // getStackTrace() itself
            .firstOrNull { it.className != this::class.java.name }
            ?.methodName
            ?: "This operation"
        throw UnsupportedOperationException("$callerName cannot use be used with dynamic connections.")
    }

    override val isDynamic = true
    override fun forEachNeighbour(node: Int, action: (Int) -> Unit) = forEachNeighbourOf(node, action)
    override fun forEachEdge(node: Int, action: (Double, Int) -> Unit) = forEachNeighbourOf(node) { action(1.0, it) }

    override fun neighbours(node: Int): IntArray {
        val neighbours = IntArrayList()
        forEachNeighbourOf(node) { neighbours.add(it) }
        return neighbours.intArray()
    }
    override fun edges(): List<Triple<Int, Int, Double>> = unsupported()
    override fun nodes(): IntArray = unsupported()
    override fun weights(node: Int): DoubleArray = unsupported()
    override fun deepCopy(): AdjacencyList = unsupported()
    override fun reversed(): AdjacencyList = unsupported()
}
