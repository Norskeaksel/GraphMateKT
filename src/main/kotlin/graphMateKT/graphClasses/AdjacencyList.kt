package graphMateKT.graphClasses

import graphMateKT.Edges

interface AdjacencyList {

    fun nodes(): IntArray
    fun getNeighbours(node: Int): IntArray
    fun getEdges(node: Int): Edges
    fun deepCopy(): AdjacencyList
    fun reversed(): AdjacencyList
    val size: Int
}

