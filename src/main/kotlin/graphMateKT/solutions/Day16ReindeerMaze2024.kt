package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.graphClasses.Grid
import me.tongfei.progressbar.ProgressBar
import java.io.InputStream

internal fun main() {
    val (grid, ans) = Day16ReindeerMaze2024(System.`in`)
    println(ans)
    val ans2 = Day16ReindeerMaze2024Part2(grid, ans.toDouble())
    println(ans2)
    System.out.flush()
}

var dim = 0

/** Solves https://adventofcode.com/2024/day/16 */
internal fun Day16ReindeerMaze2024(inputStream: InputStream): Pair<Grid, Int> {
    val scanner = InputReader(inputStream)
    val wall = '#'
    val input = generateSequence { scanner.nextLine() }.toList().map { (it.replace("\r", "")).repeat(4) }
    val grid = Grid(input)
    dim = grid.width / 4
    grid.deleteNodesWithData(wall)
    grid.nodes().forEach { u ->
        for (i in dim..dim * 3 step dim) {
            val v = u.copy(x = (u.x + i) % (dim * 4))
            grid.addEdge(u, v, 1000)
        }
        val v = when {
            u.x < dim -> grid.xy2Node(u.x + 1, u.y)
            u.x < dim * 2 -> grid.xy2Node(u.x, u.y - 1)
            u.x < dim * 3 -> grid.xy2Node(u.x - 1, u.y)
            else -> grid.xy2Node(u.x, u.y + 1)
        } ?: return@forEach
        grid.addEdge(u, v)
    }
    val start = grid.nodes().first { it.data == 'S' }
    grid.dijkstra(start)
    val goal = grid.nodes().filter { it.data as Char == 'E' }.minBy { grid.distanceTo(it) }
    val finalPath = grid.getPath(goal)!!
    // grid.visualizeGrid(finalPath = finalPath, screenWidthMultiplier = 2.0, startPaused = true)
    return grid to grid.distanceTo(goal).toInt()
}

internal fun Day16ReindeerMaze2024Part2(grid: Grid, optimalDistance: Double): Int {
    val distanceFromStartToNode = grid.nodes().associateWith { grid.distanceTo(it) }
    val goals = grid.nodes().filter { it.data as Char == 'E' }
    val nodesInAnOptimalPath = mutableSetOf<Tile>()
    ProgressBar.wrap(grid.nodes(), "Part 2").forEach { t ->
        grid.dijkstra(t)
        if (distanceFromStartToNode[t]!! + goals.minOf { grid.distanceTo(it) } == optimalDistance) {
            nodesInAnOptimalPath.add(Tile(t.x % dim, t.y))
        }
    }
    return nodesInAnOptimalPath.size
}