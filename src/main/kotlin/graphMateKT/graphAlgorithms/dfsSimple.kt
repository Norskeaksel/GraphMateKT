package graphMateKT.graphAlgorithms

import graphMateKT.graphClasses.AdjacencyList

internal fun dfsSimple(graph: AdjacencyList, start: Int, currentlyVisited: MutableSet<Int> = mutableSetOf()): Set<Int> {
    if (start !in currentlyVisited) {
        currentlyVisited.add(start)
        graph.forEachNeighbour(start) { neighbour ->
            dfsSimple(graph, neighbour, currentlyVisited)
        }
    }
    return currentlyVisited
}
