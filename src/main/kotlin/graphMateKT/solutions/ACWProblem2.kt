package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.Graph
import java.io.InputStream

internal fun main() {
    val ans = ACWProblem2(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/ACWProblem2 */
internal fun ACWProblem2(inputStream: InputStream): Int {
    val scanner = InputReader(inputStream)
    val graph = Graph()
    generateSequence { scanner.nextLine() }.forEach { line ->
        val (u, v) = line.split(" -- ").map { it.trim() }
        graph.connect(u, v)
    }
    val startNode = graph.nodes().first()
    graph.dfs(startNode)
    val furthestNode = graph.furthestNode()
    graph.dfs(furthestNode)
    val ans = graph.distanceTo(graph.furthestNode()).toInt()
    // graph.visualizeGraph(true)
    return ans
}
