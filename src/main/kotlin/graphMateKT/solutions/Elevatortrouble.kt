package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.IntGraph
import java.io.InputStream

internal fun main() {
    val ans = elevatortrouble(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/elevatortrouble */
internal fun elevatortrouble(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val (f, s, g, u, d) = scanner.nextIntArray(5)
    val graph = IntGraph(f + 1)
    graph.connectWithRule {
        val upNode = it + u
        val downNode = it - d
        when {
            upNode > f -> if (downNode < 1) emptyList() else listOf(downNode)
            downNode < 1 -> listOf(upNode)
            else -> listOf(upNode, downNode)
        }
    }
    graph.bfs(s, g)
    val dist = graph.distanceTo(g).toInt()
    return if (dist == Int.MAX_VALUE) {
        "use the stairs"
    } else {
        dist.toString()
    }
}
