package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.IntGraph
import java.io.InputStream

internal fun main() {
    val ans = colorland(System.`in`)
    println(ans)
    System.out.flush()
}

private fun addEdgesFromPreviousColorToCurrent(graph: IntGraph, previous: IntArray, color: Int, current: Int) {
    for (i in previous[color]..current) {
        graph.addEdge(i, current)
    }
    previous[color] = current
}

/** Solves https://open.kattis.com/problems/colorland */
internal fun colorland(inputStream: InputStream): Int {
    val scanner = InputReader(inputStream)
    val n = scanner.nextInt()
    val graph = IntGraph(n + 1, n * 7)
    val previous = IntArray(6)
    repeat(n) {
        val color = scanner.nextString()
        val current = it + 1
        when (color) {
            "Blue" -> addEdgesFromPreviousColorToCurrent(graph, previous, 0, current)
            "Orange" -> addEdgesFromPreviousColorToCurrent(graph, previous, 1, current)
            "Pink" -> addEdgesFromPreviousColorToCurrent(graph, previous, 2, current)
            "Green" -> addEdgesFromPreviousColorToCurrent(graph, previous, 3, current)
            "Red" -> addEdgesFromPreviousColorToCurrent(graph, previous, 4, current)
            "Yellow" -> addEdgesFromPreviousColorToCurrent(graph, previous, 5, current)
        }
    }
    graph.bfs(0, n)
    return graph.distanceTo(n).toInt()
}
