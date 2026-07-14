package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.IntGraph
import java.io.InputStream
import java.math.BigDecimal
import java.math.RoundingMode

internal fun main() {
    val ans = ticketcompleted(System.`in`)
    println(ans.toString())
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/ticketcompleted */
internal fun ticketcompleted(inputStream: InputStream): BigDecimal {
    val scanner = InputReader(inputStream)
    val n = scanner.nextInt()
    val m = scanner.nextInt()
    val graph = IntGraph(n + 1, m * 2)
    repeat(m) {
        val u = scanner.nextInt()
        val v = scanner.nextInt()
        graph.connect(u, v)
    }
    val components = mutableListOf<List<Int>>()
    graph.nodes().forEach {
        graph.bfs(it, reset = false)
        val visited = graph.currentVisitedNodes()
        if(visited.isNotEmpty()) {
            components.add(visited)
        }
    }
    val nrBelongingToComponent = components.map { it.size }

    val possibleNrOfConnections = BigDecimal(n * (n - 1) / 2)
    val actualConnections = BigDecimal(nrBelongingToComponent.fold(0L) { acc, i -> acc + i * (i - 1) / 2 })
    return actualConnections.divide(possibleNrOfConnections, 8, RoundingMode.HALF_UP)
}
