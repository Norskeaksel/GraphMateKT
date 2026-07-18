package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.graphClasses.Grid
import java.io.InputStream
import kotlin.math.sqrt

internal fun main() {
    val ans = caveexploration2(System.`in`)
    println(ans)
    System.out.flush()
}

private fun binarySearchRequiredHeight(tileInput: Array<Tile>): Int {
    val n = sqrt(tileInput.size.toDouble()).toInt()
    val start = Tile(0, 0)
    val target = Tile(n - 1, n - 1)
    var lowerBound = 1
    var upperBound = tileInput.maxOf { it.data as Int + 1 }
    while (lowerBound < upperBound) {
        val mid = (lowerBound + upperBound) / 2
        val grid = Grid(n, n, false)
        tileInput.forEach { t ->
            if ((t.data as Int) < mid) {
                grid.addNode(t)
            }
        }
        grid.connectGridDefault()
        grid.bfs(start, target)
        if (grid.foundTarget()) {
            upperBound = mid
        } else {
            lowerBound = mid + 1
        }
    }
    return lowerBound
}

/** Solves https://open.kattis.com/problems/caveexploration2 */
internal fun caveexploration2(inputStream: InputStream): Int {
    val scanner = InputReader(inputStream)
    val n = scanner.nextInt()
    val tileInput = Array(n * n) { Tile(it % n, it / n, scanner.nextInt()) }
    return binarySearchRequiredHeight(tileInput) - 1
}
