package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.debug
import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.gridGraphics.visualizeGrid
import java.io.InputStream

internal fun main() {
    val ans = draughts(System.`in`)
    println(ans)
    System.out.flush()
}

private fun neighbours(grid: Grid, t: Tile): MutableList<Tile> {
    val enemyPieces = grid.getDiagonalNeighbours(t).filter { it.data == 'B' }
    val neighbours = mutableListOf<Tile>()
    enemyPieces.forEach { e ->
        val dx = e.x - t.x
        val dy = e.y - t.y
        val nx = e.x + dx
        val ny = e.y + dy
        val potentialNeighbour = grid.xy2Node(nx, ny)
        if (potentialNeighbour?.data in listOf('.', '#')) {
            neighbours.add(potentialNeighbour!!)
        }
    }
    return neighbours
}

/** Solves https://open.kattis.com/problems/draughts */
internal fun draughts(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val n = scanner.nextInt()
    scanner.nextLine()
    val ans = StringBuilder()
    repeat(n) {
        val lines = generateSequence { scanner.nextLine() }.takeWhile { it.isNotEmpty() }.toList()
        var grid = Grid(lines)
        val startNodes = grid.nodes().filter { it.data == 'W' }
        var maxDepth = 0
        startNodes.forEach { startNode ->
            grid = Grid(lines)
            grid.connectGrid { t ->
                neighbours(grid, t)
            }
            debug("dfs from $startNode")
            grid.dfs(startNode)
            maxDepth = grid.depth().coerceAtLeast(maxDepth) / 2
        }
        grid.visualizeGrid(screenWidthMultiplier = 3.0)
        ans.appendLine(maxDepth)
    }
    return ans.toString()
}

