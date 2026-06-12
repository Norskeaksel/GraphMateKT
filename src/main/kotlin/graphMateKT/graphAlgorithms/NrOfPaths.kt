package graphMateKT.graphAlgorithms

import graphMateKT.graphClasses.AdjacencyList

private fun memoDfs(currentId: Int, target: Int, graph: AdjacencyList, memo: LongArray, mod: Long): Long {
    if (currentId == target)
        return 1L
    if (memo[currentId] != -1L)
        return memo[currentId]
    var totalPaths = 0L
     graph.forEachNeighbour(currentId){ neighbour ->
        totalPaths = (totalPaths + memoDfs(neighbour, target, graph, memo, mod)) % mod
    }
    memo[currentId] = totalPaths % mod
    return totalPaths
}
internal fun nrOfPaths(graph: AdjacencyList, start: Int, target: Int, mod: Long): Long {
    val memo = LongArray(graph.size) { -1L }
    return memoDfs(start, target, graph, memo, mod)
}
