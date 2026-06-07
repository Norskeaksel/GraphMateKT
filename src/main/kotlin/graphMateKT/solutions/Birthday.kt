package graphMateKT.solutions

import graphMateKT.graphClasses.IntGraph
import graphMateKT.readInts

internal fun main() {
    val ans = birthday()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/birthday */
internal fun birthday(): String {
    val stringBuilder = StringBuilder()
    (0..9).forEach { _ ->
        val (p, c) = readInts(2)
        if (p == 0 && c == 0)
            return stringBuilder.toString()
        val input = mutableListOf<Pair<Int, Int>>()
        repeat(c) {
            val (a, b) = readInts(2)
            input.add(a to b)
        }
        repeat(c) { i ->
            val g = IntGraph(p, c * 2)
            repeat(c) { j ->
                if (i != j) {
                    val (a, b) = input[j]
                    g.connect(a, b)
                }
            }
            try {
                g.minimumSpanningTree() // Fails if not connected
            } catch (_: IllegalStateException) {
                stringBuilder.appendLine("Yes")
                return@forEach
            }
        }
        stringBuilder.appendLine("No")
    }
    return stringBuilder.toString()
}
