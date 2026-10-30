package graphMateKT.graphAlgorithms

import graphMateKT.graphClasses.AdjacencyList

internal interface Memo {
    operator fun get(id: Int): Long
    operator fun set(id: Int, value: Long)
}

internal class MemoArray(graphSize: Int) : Memo {
    private val memo = LongArray(graphSize) { -1L }
    override operator fun get(id: Int): Long = memo[id]
    override operator fun set(id: Int, value: Long) {
        memo[id] = value
    }
}

internal class MemoMap : Memo {
    private val memo = hashMapOf<Int, Long>()
    override operator fun get(id: Int): Long = memo[id] ?: -1L
    override operator fun set(id: Int, value: Long) {
        memo[id] = value
    }
}

private fun memoDfs(currentId: Int, target: Int, graph: AdjacencyList, memo: Memo, mod: Long): Long {
    if (currentId == target)
        return 1L
    memo[currentId].let {
        if (it != -1L)
            return it
    }
    var totalPaths = 0L
    graph.forEachNeighbour(currentId) { neighbour ->
        totalPaths = (totalPaths + memoDfs(neighbour, target, graph, memo, mod)) % mod
    }
    memo[currentId] = totalPaths % mod
    return totalPaths
}

internal fun nrOfPaths(graph: AdjacencyList, start: Int, target: Int, mod: Long): Long {
    val memo = MemoArray(graph.size)
    return memoDfs(start, target, graph, memo, mod)
}

internal fun nrOfPathsSparse(graph: AdjacencyList, start: Int, target: Int, mod: Long): Long {
    val memo = MemoMap()
    return memoDfs(start, target, graph, memo, mod)
}
