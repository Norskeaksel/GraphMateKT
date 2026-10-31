package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.Graph
import java.io.InputStream


internal fun main() {
    val ans = rubiksrevenge(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/rubiksrevenge */
internal fun rubiksrevenge(inputStream: InputStream): Int {
    val scanner = InputReader(inputStream)
    val targetState = generateSequence { scanner.nextString() }.toList().joinToString("")
    val graph = Graph()
    graph.addNode(targetState)
    val startingState = "RRRR" + "GGGG" + "BBBB" + "YYYY"
    graph.addNode(startingState)
    graph.connectWithRule(::gridMoves)
    graph.bfsBidirectional(listOf(startingState), targetState, maxDistance = 6.0, storePaths = false, storeCurrentlyVisited = false)
    return graph.distanceTo(targetState).toInt()
}

private fun gridMoves(grid: Any): List<String> {
    grid as String
    val neighbors = mutableListOf<String>()
    repeat(4) { i ->
        val leftShift = grid.toCharArray()
        repeat(4) { x ->
            leftShift[i * 4 + x] = grid[i * 4 + ((x + 1) % 4)]
        }
        neighbors.add(String(leftShift))

        val rightShift = grid.toCharArray()
        repeat(4) { x ->
            rightShift[i * 4 + x] = grid[i * 4 + ((x + 3) % 4)]
        }
        neighbors.add(String(rightShift))

        val upShift = grid.toCharArray()
        repeat(4) { y ->
            upShift[y * 4 + i] = grid[((y + 1) % 4) * 4 + i]
        }
        neighbors.add(String(upShift))

        val downShift = grid.toCharArray()
        repeat(4) { y ->
            downShift[y * 4 + i] = grid[((y + 3) % 4) * 4 + i]
        }
        neighbors.add(String(downShift))
    }
    return neighbors
}
