package graphMateKT.graphAlgorithms

import graphMateKT.UnweightedAdjacencyList

private fun memoDfs(currentId: Int, target: Int, graph: UnweightedAdjacencyList, memo: LongArray): Long {
    if (currentId == target)
        return 1L
    if (memo[currentId] != -1L)
        return memo[currentId]
    var totalPaths = 0L
     graph[currentId].forEach { neighbour ->
        totalPaths += memoDfs(neighbour, target, graph, memo)
    }
    memo[currentId] = totalPaths
    return totalPaths
}
internal fun nrOfPaths(graph: UnweightedAdjacencyList, start: Int, target: Int): Long {
    val memo = LongArray(graph.size) { -1L }
    return memoDfs(start, target, graph, memo)
}