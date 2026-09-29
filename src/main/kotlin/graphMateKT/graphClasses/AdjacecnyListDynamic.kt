package graphMateKT.graphClasses

internal class AdjacecnyListDynamic(override val size: Int, val getNeighbours: (Int) -> IntArray) : AdjacencyList {
    private fun unsupported(): Nothing {
        val callerName = Thread.currentThread().stackTrace
            .drop(1) // getStackTrace() itself
            .firstOrNull { it.className != this::class.java.name }
            ?.methodName
            ?: "This operation"
        throw UnsupportedOperationException("$callerName cannot use be used with dynamic connections.")
    }

    override val isDynamic = true
    override fun forEachNeighbour(node: Int, action: (Int) -> Unit) {
        getNeighbours(node).forEach(action)
    }

    override fun forEachEdge(node: Int, action: (Double, Int) -> Unit) {
        getNeighbours(node).forEach {
            action(1.0, it)
        }
    }

    override fun neighbours(node: Int): IntArray = getNeighbours(node)
    override fun edges(): List<Triple<Int, Int, Double>> = unsupported()
    override fun nodes(): IntArray = unsupported()
    override fun weights(node: Int): DoubleArray = unsupported()
    override fun deepCopy(): AdjacencyList = unsupported()
    override fun reversed(): AdjacencyList = unsupported()
}
