package graphMateKT.solutions

import graphMateKT.Edge
import graphMateKT.Edges
import graphMateKT.graphAlgorithms.Dijkstra
import graphMateKT.graphClasses.NestedAdjacencyList
import graphMateKT.readInts

internal fun  main() {
    val ans = charlesincharge()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/charlesincharge */
internal fun  charlesincharge(): String {
    val (n, m, x) = readInts(3)
    val g: MutableList<Edges> = MutableList(n + 1) { mutableListOf() }
    repeat(m) {
        val (u, v, w) = readInts(3)
        val edgeUV: Edge = w.toDouble() to v
        val edgeVU: Edge = w.toDouble() to u
        g[u].add(edgeUV)
        g[v].add(edgeVU)
    }
    val dijkstra = Dijkstra(NestedAdjacencyList(g))
    val seachResults = dijkstra.dijkstra(1)
    val shortestPath = seachResults.distances[n]
    val maxTime = shortestPath * (1 + x.toDouble() / 100)
    return binarySearchDijkstra(g, maxTime).toString()
}

private const val INF = 1e20
private const val MAX_W = 1e9
private fun binarySearchDijkstra(g: MutableList<Edges>, maxTime: Double): Int {
    var lowerBound = g.minOf { it.minOfOrNull { e -> e.first } ?: MAX_W }.toInt()
    var upperBound = g.maxOf { it.maxOfOrNull { e -> e.first } ?: MAX_W }.toInt()
    while (upperBound - lowerBound >= 1) {
        val mid = (lowerBound + upperBound) / 2
        g.forEachIndexed { u, edges ->
            edges.forEachIndexed { v, edge ->
                if (edge.first > mid && edge.first <= MAX_W) {
                    g[u][v] = edge.first * INF to edge.second
                } else if (edge.first > MAX_W && edge.first / INF <= mid) {
                    g[u][v] = edge.first / INF to edge.second
                }
            }
        }
        val shortestPath = Dijkstra(NestedAdjacencyList(g)).dijkstra(1).distances[g.size - 1]
        if (shortestPath <= maxTime) {
            upperBound = mid
        } else {
            lowerBound = mid + 1
        }
    }
    return upperBound
}