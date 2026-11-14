package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.debug
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

    val nrOfNodeStates = 101
    val stateGraph = IntGraph(n * nrOfNodeStates)
    val answers = mutableMapOf<Triple<Int, Int, Int>, String>()
    queriesOrdered.forEach { q ->
        val (c, start, target) = q
        repeat(n) { u ->
            val edges = distanceGraph.edges(u)
            for (i in lastC until c) {
                val currentState = u * nrOfNodeStates + i
                val nextFuelState = u * nrOfNodeStates + i + 1
                stateGraph.addEdge(currentState, nextFuelState, costs[u])
            }
            for (i in lastC..c) {
                val currentState = u * nrOfNodeStates + i
                edges.forEach { (d, v) ->
                    if (i < d) return@forEach
                    val nextNeighbourState = v * nrOfNodeStates + i - d.toInt()
                    stateGraph.addEdge(currentState, nextNeighbourState, 0)
                }
            }
        }
        for (i in 100 downTo 1) {
            stateGraph.addEdge(target * nrOfNodeStates + i, target * nrOfNodeStates + i - 1, 0)
        }
        stateGraph.shortestPathsFrom(start * nrOfNodeStates, target * nrOfNodeStates)
        // debug("${stateGraph.finalPath()}")
        answers[q] =
            if (stateGraph.distanceTo(target * nrOfNodeStates).isInfinite()) "impossible"
            else stateGraph.distanceTo(target * nrOfNodeStates).toInt().toString()
        lastC = c
    }
// distanceGraph.visualizeGraph(true)
    return queries.joinToString("\n") { answers[it]!! } + "\n"
}
