package graphMateKT.graphClasses

internal interface AdjacencyList {
    fun nodes(): IntArray
    fun edges(): List<Triple<Int, Int, Double>>
    fun neighbours(node: Int): IntArray
    fun weights(node: Int): DoubleArray
    fun forEachNeighbour(node: Int, action: (Int) -> Unit)
    fun forEachEdge(node: Int, action: (Double, Int) -> Unit)
    fun deepCopy(): AdjacencyList
    fun reversed(): AdjacencyList
    val size: Int
}
