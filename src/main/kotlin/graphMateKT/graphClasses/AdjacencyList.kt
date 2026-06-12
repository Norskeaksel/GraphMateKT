package graphMateKT.graphClasses

import graphMateKT.Edges

interface AdjacencyList {

    fun nodes(): IntArray
    fun neighbours(node:Int ): IntArray
    fun edges(node: Int): Edges
    fun forEachNeighbour(node: Int, action: (Int) -> Unit)
    fun forEachEdge(node: Int, action: (Double, Int) -> Unit)
    fun deepCopy(): AdjacencyList
    fun reversed(): AdjacencyList
    val size: Int
}

