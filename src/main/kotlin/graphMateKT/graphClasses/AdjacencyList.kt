package graphMateKT.graphClasses

internal interface AdjacencyList {
    fun nodes(): IntArray
    fun neighbours(node: Int): IntArray
    fun weights(node: Int): DoubleArray
    fun forEachNeighbour(node: Int, action: (Int) -> Unit)
    fun forEachEdge(node: Int, action: (Double, Int) -> Unit)
    fun deepCopy(): AdjacencyList {
        throw NotImplementedError("deepCopy() not implemented for ${this::class.simpleName}")
    }
    fun reversed(): AdjacencyList {
        throw NotImplementedError("reversed() not implemented for ${this::class.simpleName}")
    }

    val size: Int
}

