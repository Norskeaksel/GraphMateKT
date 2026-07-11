package graphMateKT.graphClasses

import graphMateKT.Edges

/** Interface used internally in the graph glasses. Only needed to inherit from BaseGraph */
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

