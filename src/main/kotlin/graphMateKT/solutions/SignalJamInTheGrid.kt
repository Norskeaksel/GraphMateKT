package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.gridGraphics.visualizeGrid
import java.io.InputStream

internal fun main() {
    val ans = signalJamInTheGrid(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/SignalJamInTheGrid */
internal fun signalJamInTheGrid(inputStream: InputStream): Int {
    val scanner = InputReader(inputStream)
    val n = scanner.nextInt()
    val m = scanner.nextInt()
    val lines = mutableListOf<String>()
    repeat(n) {
        val line = scanner.nextLine()
        lines.add("$line#$line")
    }
    val grid = Grid(lines)
    val start = grid.nodes().first { it.data == 'S' }
    val firstEnd = grid.nodes().first { it.data == 'E' }
    val end = grid.nodes().last { it.data == 'E' }
    grid.deleteNodesWithData('#')
    grid.nodes().forEach { u ->
        val w = if (u.dataIsDigit()) u.data as Char - '0' else 0
        grid.getStraightNeighbours(u).forEach { v ->
            grid.addEdge(u, v, w)
            grid.addEdge(u, grid.xy2Node(v.x + m + 1, v.y) ?: return@forEach, 1)
        }
    }
    grid.dijkstra(start, end)
    grid.visualizeGrid()
    return if(grid.foundTarget()) grid.distanceTo(end).coerceAtMost(grid.distanceTo(firstEnd)).toInt() else -1
}
