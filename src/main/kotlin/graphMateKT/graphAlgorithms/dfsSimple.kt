package graphMateKT.graphAlgorithms

import graphMateKT.graphClasses.AdjacencyList

internal fun dfsSimple(graph: AdjacencyList, start: Int, currentVisited: MutableSet<Int> = mutableSetOf()): Set<Int> {
    if (start !in currentVisited) {
        currentVisited.add(start)
        graph.forEachNeighbour(start) { neighbour ->
            dfsSimple(graph, neighbour, currentVisited)
        }
    }
    return currentVisited
}
