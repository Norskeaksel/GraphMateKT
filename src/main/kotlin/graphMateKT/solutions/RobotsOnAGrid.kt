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
    val lines = generateSequence { scanner.nextString() }.toList()
    val grid = Grid(lines)
    grid.deleteNodesWithData('#')
    val nodes = grid.nodes()
    nodes.forEach { u ->
        grid.xy2Node(u.x + 1, u.y)?.let { v1 ->
            grid.addEdge(u, v1)
        }
        grid.xy2Node(u.x, u.y + 1)?.let { v2 ->
            grid.addEdge(u, v2)
        }
    }
    val start = nodes.first()
    val target = nodes.last()
    val nrOfPaths = grid.nrOfPaths(start, target, Int.MAX_VALUE.toLong())
    if (nrOfPaths > 0) {
        return nrOfPaths.toString()
    }
    nodes.forEach { u ->
        grid.xy2Node(u.x - 1, u.y)?.let { v1 ->
            grid.addEdge(u, v1)
        }
        grid.xy2Node(u.x, u.y - 1)?.let { v2 ->
            grid.addEdge(u, v2)
        }
    }
    grid.bfs(start, target)
    return if (grid.foundTarget()) {
        "THE GAME IS A LIE"
    } else {
        "INCONCEIVABLE"
    }
}
