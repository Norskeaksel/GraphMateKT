package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.INPUT
import graphMateKT.graphClasses.IntGraph
import graphMateKT.graphics.graphGraphics.visualizeGraph
import java.io.InputStream

internal fun main() {
    val ans = beehives2(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/beehives2 */
internal fun beehives2(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val (n, m) = scanner.nextIntArray(2)
    val graph = IntGraph(n)
    repeat(m) {
        val (u, v) = scanner.nextIntArray(2)
        graph.connect(u, v)
    }
    var ans = Double.POSITIVE_INFINITY
    graph.nodes().forEach outer@{
        graph.bfs(it)
        // graph.visualizeGraph(true)
        val seen = mutableSetOf<Int>()
        graph.currentVisitedNodes().forEach { u ->
            seen.add(u)
            graph.neighbours(u).forEach { v ->
                if (v in seen && graph.parent(u) != v) {
                    ans = (graph.distanceTo(u) + graph.distanceTo(v) + 1).coerceAtMost(ans)
                    return@outer
                }
            }
        }
    }
    return if (ans == Double.POSITIVE_INFINITY) "impossible" else ans.toInt().toString()
}
