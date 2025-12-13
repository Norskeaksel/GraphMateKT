package graphMateKT.graphAlgorithms

import graphMateKT.UnweightedAdjacencyList
import java.math.BigInteger

private fun memoDfs(currentId: Int, target: Int, graph: UnweightedAdjacencyList, memo: LongArray, mod: Long): Long {
    if (currentId == target)
        return 1L
    if (memo[currentId] != -1L)
        return memo[currentId]
    var totalPaths = 0L
     graph[currentId].forEach { neighbour ->
        totalPaths = (totalPaths + memoDfs(neighbour, target, graph, memo, mod)) % mod
    }
    memo[currentId] = totalPaths % mod
    return totalPaths
}
internal fun nrOfPaths(graph: UnweightedAdjacencyList, start: Int, target: Int, mod: Long): Long {
    val memo = LongArray(graph.size) { -1L }
    return memoDfs(start, target, graph, memo, mod)
}
