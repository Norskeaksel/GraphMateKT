package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.IntGraph
import java.io.InputStream

internal fun main() {
    val ans = ticketcompleted(System.`in`)
    println(ans.toString())
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/ticketcompleted */
internal fun ticketcompleted(inputStream: InputStream): Double {
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
        if (visited.isNotEmpty()) {
            components.add(visited)
        }
    }
    val nrBelongingToComponent = components.map { it.size }

    val possibleNrOfConnections = n * (n - 1L) / 2.0
    val actualConnections = nrBelongingToComponent.fold(0.0) { acc, i -> acc + i * (i - 1L) / 2 }
    return actualConnections / possibleNrOfConnections
}
