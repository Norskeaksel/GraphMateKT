package graphMateKT.solutions

import graphMateKT.readInt
import graphMateKT.readLine

var maxDepth = 0

internal fun main() {
    val ans = draughts()
    println(ans)
    System.out.flush()
}

private data class XY(val x: Int, val y: Int) {
    fun isOutOfBounds(width: Int, height: Int) = x !in 0..<width || y !in 0..<height
    fun pointBetweenHereAndPoint(p: XY) = XY((x + p.x) / 2, (y + p.y) / 2)
}

private fun neighbours(grid: List<String>, p: XY): MutableList<XY> {
    val width = grid[0].length
    val height = grid.size
    val neighbours = mutableListOf<XY>()
    for (dx in -1..1 step 2) {
        for (dy in -1..1 step 2) {
            val dp = XY(p.x + dx, p.y + dy)
            if (dp.isOutOfBounds(width, height) || grid[dp.y][dp.x] != 'B')
                continue
            val nx = p.x + dx * 2
            val ny = p.y + dy * 2
            val np = XY(nx, ny)
            if (!np.isOutOfBounds(width, height) && grid[np.y][np.x] in listOf('.', '#', 'X'))
                neighbours.add(np)
        }
    }
    return neighbours
}

private fun depthOfDFSOnCheckersBoard(board: List<String>, cp: XY, depth: Int = 0){
    val neighbours = neighbours(board, cp)
    neighbours.forEach { np ->
        val bp = cp.pointBetweenHereAndPoint(np)
        val gridWithoutCapturedPiece = board.mapIndexed { y, row ->
            if (y == bp.y) row.replaceRange(bp.x, bp.x + 1, "X")
            else if (y == np.y) row.replaceRange(np.x, np.x + 1, "X")
            else row
        }
        /* val grid = Grid(board)
        val currentVisitedNodes = grid.nodes().filter { it.data == 'B' }
        val finalPath = grid.nodes().filter { it.data == 'X' }
        grid.visualizeGrid(currentVisitedNodes, finalPath = finalPath, animationTicTimeOverride = 1.0) */
        depthOfDFSOnCheckersBoard(gridWithoutCapturedPiece, np, depth + 1)
    }
    maxDepth = depth.coerceAtLeast(maxDepth)
}

private fun List<String>.clearPoint(cp: XY) = mapIndexed { y, row ->
    if (y == cp.y) row.replaceRange(cp.x, cp.x + 1, "X") else row
}

/** Solves https://open.kattis.com/problems/draughts */
internal fun draughts(): String {
    val n = readInt()
    val ans = mutableListOf<Int>()
    readLine()
    repeat(n) {
        maxDepth = 0
        val board = generateSequence { readLine() }.takeWhile { it.isNotBlank() }.toList()
        val startNodes = board.flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, char -> if (char == 'W') XY(x, y) else null }
        }

        startNodes.forEach { start ->
            val clearedBoard = board.clearPoint(start)
            depthOfDFSOnCheckersBoard(clearedBoard, start)
        }
        ans.add(maxDepth)
    }
    return ans.joinToString("\n")
}
