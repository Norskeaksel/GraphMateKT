package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.debug
import graphMateKT.graphClasses.IntGraph
import graphMateKT.graphics.graphGraphics.visualizeGraph
import java.io.InputStream

internal fun main() {
    val ans = promotions(System.`in`)
    println(ans)
    System.out.flush()
}

private fun findLastLessOrEqual(accDistances: List<Int>, target: Int): Int {
    val index = accDistances.binarySearch(target)
    return if (index >= 0) accDistances[index] // Target is found in the list; index is non-negative
    else {
        val insertionIndex = -index - 1
        val targetIndex = insertionIndex - 1
        if (targetIndex >= 0) accDistances[targetIndex] else 0
    }
}

/** Solves https://open.kattis.com/problems/promotions */
internal fun promotions(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val (a, b, e, p) = scanner.nextIntArray(4)
    val graph = IntGraph(e, p)
    val reversedGraph = IntGraph(e, p)
    repeat(p) {
        val u = scanner.nextInt()
        val v = scanner.nextInt()
        graph.addEdge(u, v)
        reversedGraph.addEdge(v, u)
    }
    val startNodes = reversedGraph.nodes().filter { reversedGraph.neighbours(it).isEmpty() }
    graph.bfs(startNodes)
    //graph.visualizeGraph()
    val nrOfDistances = IntArray(e)
    graph.nodes().forEach { node ->
        nrOfDistances[graph.distanceTo(node).toInt()]++
    }
    val accDistances = nrOfDistances.runningReduce { acc, next -> acc + next }
    debug(accDistances)
    return """${findLastLessOrEqual(accDistances, a)}
${findLastLessOrEqual(accDistances, b)}
"""
}
