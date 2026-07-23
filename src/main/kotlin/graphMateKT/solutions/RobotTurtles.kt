package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.debug
import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.gridGraphics.visualizeGrid
import java.io.InputStream

internal fun main() {
    val ans = robotTurtles(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/robotTurtles */
internal fun robotTurtles(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val input = generateSequence { scanner.nextLine() }.toList().map { (it.replace("\r", "") + 'C').repeat(4) }
    val grid = Grid(input)
    grid.deleteNodesWithData('C')
    val dim = 9
    grid.nodes().forEach { u ->
        for (i in dim..dim * 3 step dim) {
            val v = u.copy(x = (u.x + i) % (dim * 4))
            grid.addEdge(u, v)
        }
        val v = when {
            u.x < dim -> grid.xy2Node(u.x + 1, u.y)
            u.x < dim * 2 -> grid.xy2Node(u.x, u.y + 1)
            u.x < dim * 3 -> grid.xy2Node(u.x - 1, u.y)
            else -> grid.xy2Node(u.x, u.y - 1)
        } ?: return@forEach
        val w = if (v.data as Char == 'I') 2 else 1
        grid.addEdge(u, v, w)
    }
    val start = Tile(0, 7, 'T')
    grid.dijkstra(start)
    val goal = grid.nodes().filter { it.data as Char == 'D' }.minBy { grid.distanceTo(it) }
    val finalPath = grid.getPath(goal) ?: return "no solution"
    // grid.visualizeGrid(finalPath = finalPath, screenWidthMultiplier = 2.0)
    val ans = StringBuilder()
    finalPath.let {
        it.forEachIndexed { i, t ->
            if (i == 0) return@forEachIndexed
            val u = it[i - 1]
            val v = it[i]
            ans.append(
                when {
                    v.x == (u.x + dim) % (dim * 4) -> "R"
                    v.x == (u.x + dim * 3) % (dim * 4) -> "L"
                    v.data as Char == 'I' -> "XF"
                    else -> "F"
                }
            )
        }
    }
    debug(finalPath.map { it.run { "$x $y" } })
    debug(ans.toString())
    return ans.toString()
}
