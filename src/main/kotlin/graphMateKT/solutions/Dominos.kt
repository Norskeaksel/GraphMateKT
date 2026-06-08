package graphMateKT.solutions

import InputReader
import graphMateKT.graphClasses.IntGraph
import java.io.InputStream

internal fun main() {
    val ans = dominos(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/dominos */
internal fun dominos(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val c = scanner.nextInt()
    val ans = StringBuilder()
    repeat(c) {
        val n = scanner.nextInt()
        val m = scanner.nextInt()
        val graph = IntGraph(n, m, true)
        repeat(m) {
            val u = scanner.nextInt()
            val v = scanner.nextInt()
            graph.addEdge(u - 1, v - 1)
        }
        val components = graph.stronglyConnectedComponents()
        val nodeToComponentId = IntArray(n)
        components.forEachIndexed { id, component ->
            component.forEach { node ->
                nodeToComponentId[node] = id
            }
        }
        val hasIncomingEdge = BooleanArray(components.size)
        repeat(n) { node ->
            val uId = nodeToComponentId[node]
            graph.neighbours(node).forEach { neighbour ->
                val vId = nodeToComponentId[neighbour]
                if (vId != uId) {
                    hasIncomingEdge[vId] = true
                }
            }
        }
        val flips = hasIncomingEdge.count { !it }
        ans.appendLine(flips)
    }
    return ans.toString()
}
