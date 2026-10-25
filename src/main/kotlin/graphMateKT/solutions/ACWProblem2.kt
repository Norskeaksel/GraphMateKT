package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.debug
import graphMateKT.graphClasses.Graph
import graphMateKT.graphics.graphGraphics.visualizeGraph
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
    graph.bfs(startNode, maxDistance = 3.0)
    /*val subGraph = Graph().apply {
        graph.visitedNodes().forEach { u ->
            graph.forEachNeighbour(u) { v ->
                addEdge(u, v)
            }
        }
    }
    subGraph.visualizeGraph(true, listOf(startNode), "Subgraph of ACW Problem 2") */
    debug("Nr of nodes: ${graph.nodes().size}")
    debug("Nr of edges: ${graph.edges().size / 2}")
    graph.dfs(startNode)
    val furthestNode = graph.furthestNode()
    graph.dfs(furthestNode)
    val ans = graph.distanceTo(graph.furthestNode()).toInt()
    // graph.visualizeGraph(true)
    return ans
}
