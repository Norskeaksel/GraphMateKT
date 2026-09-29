package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.Grid
import java.io.InputStream

internal fun main() {
    val ans = RobotsOnAGrid(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/RobotsOnAGrid. Note: This solution gets TLE without an AI rewrite */
internal fun RobotsOnAGrid(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val n = scanner.nextInt()
    val lines = mutableListOf<String>()
    repeat(n) {
        lines.add(scanner.nextString()!!)
    }
    val grid = Grid(lines)
    grid.deleteNodesWithData('#')
    grid.connectWithRule { t ->
        grid.getStraightNeighbours(t).filter { it.x > t.x || it.y > t.y }
    }
    val nodes = grid.nodes()
    val start = nodes.first()
    val target = nodes.last()
    val nrOfPaths = grid.nrOfPaths(start, target, Int.MAX_VALUE.toLong())
    if (nrOfPaths > 0) {
        return nrOfPaths.toString()
    }
    grid.connectGridDefault()
    grid.bfs(start, target)
    return if (grid.foundTarget()) {
        "THE GAME IS A LIE"
    } else {
        "INCONCEIVABLE"
    }
}

