package graphMateKT.graphClasses

internal interface AdjacencyList {
    private class IncompatibleAlgorithm(message: String) : Exception(message)

    val size: Int
    val isDynamic: Boolean
    fun forEachNeighbour(node: Int, action: (Int) -> Unit)
    fun forEachEdge(node: Int, action: (Double, Int) -> Unit)
    fun nodes(): IntArray
    fun edges(): List<Triple<Int, Int, Double>>
    fun neighbours(node: Int): IntArray
    fun weights(node: Int): DoubleArray
    fun deepCopy(): AdjacencyList
    fun reversed(): AdjacencyList
}
