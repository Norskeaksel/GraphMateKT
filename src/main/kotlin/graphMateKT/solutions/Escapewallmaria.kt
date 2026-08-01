package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.Grid
import java.io.InputStream

internal fun main() {
    val ans = escapewallmaria(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/escapewallmaria */
internal fun escapewallmaria(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val (t, n, m) = scanner.nextIntArray(3)
    val lines = generateSequence { scanner.nextString() }.toList()
    val grid = Grid(lines)
    grid.connectGrid { t ->
        grid.getStraightNeighbours(t).filter {
            it.data == '0' ||
                    it.y < t.y && it.data == 'D' ||
                    it.x < t.x && it.data == 'R' ||
                    it.x > t.x && it.data == 'L' ||
                    it.y > t.y && it.data == 'U'
        }
    }
    val start = grid.nodes().first { it.data == 'S' }
    grid.bfs(start)
    val minDistance =
        grid.nodes().filter { it.run { x == 0 || x == m - 1 || y == 0 || y == n - 1 } }.minOf { grid.distanceTo(it) }
    return if (minDistance <= t) {
        minDistance.toInt().toString()
    } else "NOT POSSIBLE"
}
