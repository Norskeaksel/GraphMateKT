package graphMateKT.solutions

import graphMateKT.UnboxedEdges
import graphMateKT.graphAlgorithms.Dijkstra
import graphMateKT.graphClasses.NestedAdjacencyList
import graphMateKT.readInts

internal fun main() {
    val ans = charlesincharge()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/charlesincharge */
internal fun charlesincharge(): String {
    val (n, m, x) = readInts(3)
    val g = UnboxedEdges()
    repeat(m) {
        val (u, v, w) = readInts(3)
        g.addEdge(u, v, w.toDouble())
        g.addEdge(v, u, w.toDouble())
    }
    val dijkstra = Dijkstra(NestedAdjacencyList(n + 1, g))
    val seachResults = dijkstra.dijkstra(1)
    val shortestPath = seachResults.distances[n]
    val maxTime = shortestPath * (1 + x.toDouble() / 100)
    return binarySearchDijkstra(g, n, maxTime).toString()
}

private const val INF = 1e20
private const val MAX_W = 1e9
private fun binarySearchDijkstra(g: UnboxedEdges, n: Int, maxTime: Double): Int {
    var lowerBound = (0 until g.size).minOf { g.weights[it] }.toInt()
    var upperBound = (0 until g.size).maxOf { g.weights[it] }.toInt()
    while (upperBound - lowerBound >= 1) {
        val mid = (lowerBound + upperBound) / 2
        for (i in 0 until g.size) {
            val w = g.weights[i]
            if (w > mid && w <= MAX_W) {
                g.weights.doubleArray()[i] = w * INF
            } else if (w > MAX_W && w / INF <= mid) {
                g.weights.doubleArray()[i] = w / INF
            }
        }
        val shortestPath = Dijkstra(NestedAdjacencyList(n + 1, g)).dijkstra(1).distances[n]
        if (shortestPath <= maxTime) {
            upperBound = mid
        } else {
            lowerBound = mid + 1
        }
    }
    return upperBound
}