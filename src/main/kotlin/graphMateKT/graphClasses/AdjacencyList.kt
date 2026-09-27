package graphMateKT.graphClasses

internal interface AdjacencyList {
    fun forEachNeighbour(node: Int, action: (Int) -> Unit)
    fun forEachEdge(node: Int, action: (Double, Int) -> Unit)
    val size: Int

    fun nodes(): IntArray {
        throw NotImplementedError("nodes() not implemented for ${this::class.simpleName}")
    }

    fun edges(): List<Triple<Int, Int, Double>> {
        throw NotImplementedError("edges() not implemented for ${this::class.simpleName}")
    }

    fun neighbours(node: Int): IntArray {
        throw NotImplementedError("neighbours() not implemented for ${this::class.simpleName}")
    }

    fun weights(node: Int): DoubleArray {
        throw NotImplementedError("weights() not implemented for ${this::class.simpleName}")
    }


    fun deepCopy(): AdjacencyList {
        throw NotImplementedError("deepCopy() not implemented for ${this::class.simpleName}")
    }

    fun reversed(): AdjacencyList {
        throw NotImplementedError("reversed() not implemented for ${this::class.simpleName}")
    }
}