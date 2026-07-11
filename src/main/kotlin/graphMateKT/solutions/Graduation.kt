package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.Graph
import java.io.InputStream

internal fun main() {
    val ans = graduation(System.`in`)
    println(ans)
    System.out.flush()
}

private data class Node(val charSet:Set<Char>, val column: Int)

/** Solves https://open.kattis.com/problems/Graduation */
internal fun graduation(inputStream: InputStream): Int {
    val scanner = InputReader(inputStream)
    val (n, m, _) = scanner.nextIntArray(3)
    val lines = generateSequence { scanner.nextString() }.toList()
    val graph = Graph()
    for (x in 0 until m) {
        val charSet = mutableSetOf<Char>()
        for (y in 0 until n) {
            charSet.add(lines[y][x])
        }
        graph.addNode(Node(charSet, x))
    }
    val nodes = graph.nodes()
    for (x in 0 until m) {
        val node = nodes[x] as Node
        for (j in 0  until m) {
            val possibleNeighbour = nodes[j] as Node
            if(j!=x && node.charSet.any { it in possibleNeighbour.charSet}) {
                graph.addEdge(node, possibleNeighbour)
            }
        }
    }
    val components = graph.stronglyConnectedComponents()
    return components.size
}
