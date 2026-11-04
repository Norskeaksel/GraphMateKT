package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.IntGraph
import java.io.InputStream

internal fun main() {
    val ans = ironcoal(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/ironcoal */
internal fun ironcoal(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val (n, m, k) = scanner.nextIntArray(3)
    val ironNodes = scanner.nextIntArray(m).toSet()
    val coalNodes = scanner.nextIntArray(k).toSet()
    val graph = IntGraph(n + 1)
    val reversedGraph = IntGraph(n + 1)
    repeat(n) { u ->
        val a = scanner.nextInt()
        val neighbours = scanner.nextIntArray(a)
        neighbours.forEach { v ->
            graph.addEdge(u + 1, v)
            reversedGraph.addEdge(v, u + 1)
        }
    }
    graph.bfs(1)
    // graph.visualizeGraph()
    val visitedNodes = graph.visitedNodes()
    val visitedCoalNodes = visitedNodes.intersect(coalNodes).toList()
    val visitedIronNodes = visitedNodes.intersect(ironNodes).toList()
    if (visitedCoalNodes.isEmpty() || visitedIronNodes.isEmpty()) {
        return "impossible"
    }
    visitedNodes.forEach { u ->
        val v = graph.parent(u) ?: return@forEach
        graph.addEdge(u, v)
    }
    val distFromPalace = DoubleArray(visitedNodes.size) { graph.distanceTo(visitedNodes[it]) }
    reversedGraph.bfs(visitedCoalNodes)
    val distFromCoal = DoubleArray(visitedNodes.size) { reversedGraph.distanceTo(visitedNodes[it]) }
    reversedGraph.bfs(visitedIronNodes)
    val distFromIron = DoubleArray(visitedNodes.size) { reversedGraph.distanceTo(visitedNodes[it]) }
    val distSums = DoubleArray(visitedNodes.size) { i -> distFromPalace[i] + distFromCoal[i] + distFromIron[i] }
    return distSums.min().toInt().toString()
}
