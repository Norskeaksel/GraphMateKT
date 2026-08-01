package graphMateKT.solutions

import graphMateKT.graphClasses.Graph
import graphMateKT.readInt
import graphMateKT.readStrings
import java.util.*
import kotlin.math.max


internal fun main() {
    print(repostsBFS())
}

/** Solves https://codeforces.com/problemset/problem/522/A */
internal fun repostsBFS(): Int {
    val n = readInt()
    val g = Graph()
    repeat(n) {
        val (v, _, u) = readStrings(3).map { it.lowercase(Locale.getDefault()) }
        g.addEdge(u, v, 1.0)
    }
    var longestChain = 0
    g.nodes().forEach{ node ->
        g.bfs(node)
        longestChain = max(longestChain, g.depth() + 1)
    }
    return longestChain
}
