package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.gridGraphics.visualizeGrid
import java.io.InputStream
import kotlin.math.abs

internal fun main() {
    val ans = gridmst(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/gridmst */
// TODO fix the solution
internal fun gridmst(inputStream: InputStream, maxWidth: Int = 1000, maxHeight: Int = 1000): String {
    val scanner = InputReader(inputStream)
    val n = scanner.nextInt()
    val grid = Grid(maxWidth, maxHeight)
    val pointNodes = mutableListOf<Tile>()
    repeat(n) {
        val x = scanner.nextInt()
        val y = scanner.nextInt()
        val node = Tile(x, y)
        pointNodes.add(node)
    }
    grid.connectGridDefault()
    grid.nodes().forEach { node ->
        grid.bfs(node, reset = false)
    }
    return ""
}
