package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.IntGraph
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min

internal fun main() {
    val ans = forestfruits(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/forestfruits */
internal fun forestfruits(inputStream: InputStream): Long {
    val scanner = InputReader(inputStream)
    val (v, e, c, k, m) = scanner.nextIntArray(5)
    if (c < k && k <= m) {
        return -1L
    }
    val graph = IntGraph(v + 1)
    repeat(e) {
        graph.connect(scanner.nextInt(), scanner.nextInt(), scanner.nextInt())
    }
    val fruitPoints = scanner.nextIntArray(c)
    graph.dijkstra(1)
    val fruitPointsSorted = fruitPoints.sortedBy { graph.distanceTo(it) }
    val lastlyNeededFruitIdx = min(k - 1, m - 1)
    if (lastlyNeededFruitIdx >= fruitPointsSorted.size) {
        return -1L
    }
    val distanceToLastlyNeededFruit = graph.distanceTo(fruitPointsSorted[lastlyNeededFruitIdx])
    val ans = max(distanceToLastlyNeededFruit.toLong() * 2, -1)
    return ans
}
