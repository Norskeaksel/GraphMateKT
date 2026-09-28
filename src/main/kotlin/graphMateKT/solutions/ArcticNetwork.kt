package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.graphClasses.Graph
import java.io.InputStream
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

internal fun main() {
    val ans = arcticNetwork(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/ArcticNetwork */
internal fun arcticNetwork(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val n = scanner.nextInt()
    val ans = StringBuilder()
    repeat(n) {
        val graph = Graph()
        val (s, p) = scanner.nextIntArray(2)
        repeat(p) {
            val x = scanner.nextInt()
            val y = scanner.nextInt()
            graph.addNode(Tile(x, y))
        }
        graph.nodes().forEach { u ->
            graph.nodes().forEach { v ->
                if (u == v) return@forEach
                val w = (u as Tile).let { p1 ->
                    (v as Tile).let { p2 ->
                        sqrt((p1.x - p2.x).toDouble().pow(2.0) + (p2.y - p1.y).toDouble().pow(2.0))
                    }
                }
                graph.addEdge(u, v, w)
            }
        }
        val (_, mst) = graph.minimumSpanningTree()
        val edges = mst.edges().sortedBy { it.third }
        // mst.visualizeGraph(true)
        val requiredD = edges[p - 2 - (s - 1).coerceAtLeast(0)].third
        ans.appendLine(String.format(Locale.US, "%.2f", requiredD))
    }
    return ans.toString()
}
