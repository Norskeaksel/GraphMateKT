package graphMateKT.solutions

import graphMateKT.graphClasses.IntGraph
import graphMateKT.readInt
import graphMateKT.readInts

internal fun main() {
    val ans = dominos()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/dominos */
internal fun dominos(): String {
    val c = readInt()
    val ans = StringBuilder()
    repeat(c) {
        val (n, m) = readInts(2)
        val graph = IntGraph(n, false, true)
        repeat(m) {
            val (u, v) = readInts(2)
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
