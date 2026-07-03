package graphMateKT.solutions

import graphMateKT.graphClasses.Graph
import graphMateKT.readInt
import graphMateKT.readString

internal fun main() {
    val ans = torn2pieces()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/torn2pieces */
internal fun torn2pieces(): String {
    val n = readInt()
    val graph = Graph()
    repeat(n) {
        val stations = readString().split(" ").toMutableList()
        val fromStation = stations.removeFirst()
        stations.forEach { toStation ->
            graph.connect(fromStation, toStation, 1.0)
        }
    }
    val (start, end) = readString().split(" ")
    graph.addNode(start)
    graph.addNode(end)
    graph.bfs(start, end)
    val path = graph.getPath(end)
    return path?.joinToString(" ") ?: "no route found"
}
