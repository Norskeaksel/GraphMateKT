package graphMateKT.solutions

import graphMateKT.Edge
import graphMateKT.graphAlgorithms.Dijkstra
import graphMateKT.graphClasses.Graph
import graphMateKT.readInt
import graphMateKT.readInts
import java.util.*


/*internal fun main() {
    val ans = speedyescape()
    if (ans == -1.0)
        println("IMPOSSIBLE")
    else
        println(String.format(Locale.US, "%.7f", ans))
}

/** Solves https://open.kattis.com/problems/speedyescape */
internal fun speedyescape(): Double {
    val (n, m, e) = readInts(3)
    val g: AdjacencyList = MutableList(n) { mutableListOf() }
    val visualGraph = Graph()
    repeat(m) {
        var (u, v, l) = readInts(3); u--; v--
        val edgeUV: Edge = l.toDouble() to v
        val edgeVU: Edge = l.toDouble() to u
        g[u].add(edgeUV)
        g[v].add(edgeVU)
        visualGraph.connect(u, v, l.toDouble())
    }
    val ends = IntArray(e)
    repeat(e) {
        ends[it] = readInt() - 1
    }
    var (b, p) = readInts(2); b--; p--
    val gCopy = g.deepCopy()
    gCopy.setInfAsWeightsToNodes(setOf(p))
    val bDistances = Dijkstra(gCopy).dijkstra(b).distances
    if (ends.all { bDistances[it] == Double.POSITIVE_INFINITY }) return -1.0
    val pDistances = Dijkstra(g).dijkstra(p).distances
    val speedRatio = binarySearchDijkstra(b, g, pDistances, ends)
    return speedRatio * 160
}

private fun binarySearchDijkstra(b: Int, g: AdjacencyList, pDistances: DoubleArray, ends: IntArray): Double {
    var speedRatioUpperBound = 10_000.0
    var speedRationLowerBound = 0.0
    repeat(100) {
        val speedRatio = (speedRatioUpperBound + speedRationLowerBound) / 2
        val unInterceptedNodes = unInterceptedNodes(g, b, pDistances, speedRatio)
        if (ends.any { it in unInterceptedNodes }) {
            speedRatioUpperBound = speedRatio
        } else {
            speedRationLowerBound = speedRatio
        }
    }
    return speedRationLowerBound
}

private fun unInterceptedNodes(
    g: AdjacencyList,
    b: Int,
    pDistances: DoubleArray,
    speedRatio: Double
): Set<Int> {
    val gCopy = g.deepCopy()
    var bDistances = Dijkstra(gCopy).dijkstra(b).distances.map { it / speedRatio }

    val bannedNodes = mutableSetOf<Int>()
    while (bDistances.indices.any { bDistances[it] != Double.POSITIVE_INFINITY && bDistances[it] >= pDistances[it] }) {
        pDistances.indices.forEach {
            if (bDistances[it] >= pDistances[it]) bannedNodes.add(it)
        }
        gCopy.setInfAsWeightsToNodes(bannedNodes)
        bDistances = Dijkstra(gCopy).dijkstra(b).distances.map { it / speedRatio }
    }
    pDistances.indices.forEach {
        if (bDistances[it] >= pDistances[it]) bannedNodes.add(it)
    }
    val unBannedNodes = bDistances.indices.filter { it !in bannedNodes }
    return unBannedNodes.toSet()
}

private fun AdjacencyList.setInfAsWeightsToNodes(bannedNodes: Set<Int>) {
    forEachIndexed { u, edges ->
        edges.forEachIndexed { edgeIdx, edge ->
            val v = edge.second
            if (v in bannedNodes) {
                this[u][edgeIdx] = Double.POSITIVE_INFINITY to v
            }
        }
    }
}
*/