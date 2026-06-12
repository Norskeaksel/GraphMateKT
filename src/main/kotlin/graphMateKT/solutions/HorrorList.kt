package graphMateKT.solutions

import graphMateKT.graphClasses.IntGraph
import graphMateKT.readInts

internal fun main() {
    val ans = horrorList()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/horror */
internal fun horrorList(): Int {
    val (n, h, l) = readInts(3)
    val intGraph = IntGraph(n, l * 2)
    val startIds = readInts(h)
    repeat(l) {
        val (u, v) = readInts(2)
        intGraph.connect(u, v)
    }
    intGraph.bfs(startIds)
    return intGraph.furthestNode()
}
