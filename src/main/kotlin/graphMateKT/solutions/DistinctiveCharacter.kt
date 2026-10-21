package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.IntGraph
import java.io.InputStream
import kotlin.math.pow

internal fun Int.flipBit(i: Int): Int {
    return this xor (1 shl i)
}

internal fun main() {
    val ans = distinctiveCharacter(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/distinctiveCharacter */
internal fun distinctiveCharacter(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val (n, f) = scanner.nextIntArray(2)
    val startNodes = IntArray(n)
    repeat(n) {
        val bits = scanner.nextString()!!
        startNodes[it] = bits.toInt(2)
    }
    val nrOfCombinations = 2.0.pow(f).toInt()
    val graph = IntGraph(nrOfCombinations)
    graph.connectWithRule { u ->
        buildList {
            repeat(f) { i ->
                add(u.flipBit(i))
            }
        }
    }
    graph.bfs(startNodes.asList())
    return graph.furthestNode().toString(2).padStart(f, '0')
}
