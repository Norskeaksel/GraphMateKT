package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.debug
import graphMateKT.graphClasses.Graph
import java.io.InputStream
import kotlin.math.abs

internal fun main() {
    val ans = marblestree(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/marblestree */
internal fun marblestree(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val sb = StringBuilder()
    while (true) {
        val n = scanner.nextInt()
        val nrOfMarbles = IntArray(n)
        if (n == 0)
            break
        val graph = Graph()
        val connections = mutableListOf<Pair<Int, Int>>()
        repeat(n) {
            val (u, m, d) = scanner.nextIntArray(3)
            nrOfMarbles[u - 1] = m
            graph.addNode(u - 1 to m)
            val children = scanner.nextIntArray(d)
            children.forEach { v ->
                connections.add(u - 1 to v - 1)
                //graph.connect(u - 1, v - 1)
            }
        }
        connections.forEach { (u, v) ->
            graph.connect(u to nrOfMarbles[u], v to nrOfMarbles[v])
        }
        graph.dfs(0 to nrOfMarbles[0])
        graph.dfs(graph.furthestNode())
        val nodeDepths = graph.nodes().groupBy { graph.distanceTo(it) }
        var currentDepth = graph.depth().toDouble() + 1
        var ans = 0
        // debug("Initial marbles: ${nrOfMarbles.withIndex().map { "${it.index}:${it.value}" }.joinToString()}")
        while (--currentDepth > 0) {
            nodeDepths[currentDepth]!!.forEach { u ->
                val parent = graph.parent(u) ?: return@forEach
                parent as Pair<Int, Int>
                val marbleGift = nrOfMarbles[(u as Pair<Int, Int>).first] - 1
                // debug("node: ${u.first}, gives parent: ${parent.first}, marbles: $marbleGift")
                nrOfMarbles[parent.first] += marbleGift
                ans += abs(marbleGift)
            }
        }
        // graph.visualizeGraph(true)
        sb.appendLine(ans)
    }
    return sb.toString()
}
