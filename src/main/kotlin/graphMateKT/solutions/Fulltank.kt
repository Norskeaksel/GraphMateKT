package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.IntGraph
import java.io.InputStream

internal fun main() {
    val ans = fulltank(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/fulltank */
internal fun fulltank(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val (n, m) = scanner.nextIntArray(2)
    val costs = scanner.nextIntArray(n)
    val distanceGraph = IntGraph(n)
    repeat(m) {
        val (u, v, d) = scanner.nextIntArray(3)
        distanceGraph.connect(u, v, d)
    }

    val q = scanner.nextInt()
    val queries = mutableListOf<Triple<Int, Int, Int>>()
    repeat(q) {
        val (c, s, e) = scanner.nextIntArray(3)
        val start = s
        val target = e
        queries.add(Triple(c, start, target))
    }
    val queriesOrdered = queries.sortedBy { it.first }
    var lastC = 0

    val stateGraph = IntGraph(n * 1000)
    val answers = mutableMapOf<Triple<Int, Int, Int>, String>()
    queriesOrdered.forEach { q ->
        val (c, start, target) = q
        repeat(n) { u ->
            val edges = distanceGraph.edges(u)
            for (i in lastC until c) {
                val currentState = u * 1000 + i
                val nextFuelState = u * 1000 + i + 1
                stateGraph.addEdge(currentState, nextFuelState, costs[u])
            }
            for (i in lastC..c) {
                val currentState = u * 1000 + i
                edges.forEach { (d, v) ->
                    val nextNeighbourState = v * 1000 -
                    stateGraph.addEdge(currentState, nextNeighbourState, 0)
                }
            }
        }
    }
    for (i in 100 - 1 downTo 1) {
        stateGraph.addEdge(target + i, target + i - 1, 0)
    }
    lastC = c
    stateGraph.shortestPathsFrom(start, target)
    // debug("visitedNodes: ${stateGraph.visitedNodes()}")
    // stateGraph.finalPath()?.let { debug(it) }
    answers[q] = if (!stateGraph.foundTarget()) "impossible" else stateGraph.distanceTo(target).toInt().toString()
}
// distanceGraph.visualizeGraph(true)
return queries.map { answers[it] }.joinToString("\n") + "\n"
}
