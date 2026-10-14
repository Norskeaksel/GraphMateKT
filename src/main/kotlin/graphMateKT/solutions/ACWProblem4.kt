package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.gridGraphics.visualizeGrid
import java.io.InputStream

internal fun main() {
    val ans = ACWProblem4(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/AWCProblem4 */
internal fun ACWProblem4(inputStream: InputStream): Int {
    val scanner = InputReader(inputStream)
    val lines = generateSequence { scanner.nextString() }.map { "$it#" }.toList()
    val grid = Grid(lines)
    grid.deleteNodesWithData('#')
    val startNode = grid.nodes().first { it.data == 'X' }
    val startingStone = grid.nodes().first { it.data == '*' }
    val ssx = startingStone.x
    val ssy = startingStone.y
    val possibleStates = mutableListOf(lines)
    repeat(lines.size) { y ->
        repeat(lines[y].length) { x ->
            val currentTile = grid.xy2Node(x, y)
            if (currentTile.data == '#')
                return@repeat
            val mutableLines = lines.map { it.toCharArray() }.toMutableList()
            mutableLines[y][x] = '*'
            mutableLines[ssy][ssx] = '.'
            val newLines = mutableLines.map { String(it) }
            possibleStates.add(newLines)
        }
    }

    grid.bfs(startNode)
    grid.visualizeGrid()
    return 0
}
