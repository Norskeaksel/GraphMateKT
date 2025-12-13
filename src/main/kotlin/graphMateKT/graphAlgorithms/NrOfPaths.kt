package graphMateKT.graphAlgorithms

import graphMateKT.UnweightedAdjacencyList
import java.math.BigInteger

private fun memoDfs(currentId: Int, target: Int, graph: UnweightedAdjacencyList, memo: Array<BigInteger>): BigInteger {
    if (currentId == target)
        return BigInteger.ONE
    if (memo[currentId] != BigInteger.valueOf(-1L))
        return memo[currentId]
    var totalPaths = BigInteger.ZERO
     graph[currentId].forEach { neighbour ->
        totalPaths += memoDfs(neighbour, target, graph, memo)
    }
    memo[currentId] = totalPaths
    return totalPaths
}
internal fun nrOfPaths(graph: UnweightedAdjacencyList, start: Int, target: Int): BigInteger {
    val memo = Array(graph.size) { BigInteger.valueOf(-1L) }
    return memoDfs(start, target, graph, memo)
}
