/*package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.graphClasses.Grid
import graphMateKT.graphClasses.IntGraph
import graphMateKT.graphics.gridGraphics.visualizeGrid
import java.io.InputStream
import kotlin.math.pow

private data class GridState2(val person: Tile, val stone: Tile) {
    // Base 150 encoding. 150³ * x1 + 150² * y1 + 150 * x2 + y2
    fun encode() = ((person.x * base + person.y) * base + stone.x) * base + stone.y
}

internal fun main() {
    val input = generateSequence { readLine() }.toList()
    val grid = Grid(input)
    val nrOfStates = 150 * 150 * 150 * 150
    val graph = IntGraph(nrOfStates)
    val personStart = grid.nodes().first { it.data == 'X' }
    val stoneStart = grid.nodes().first { it.data == '*' }
    val target = grid.nodes().first { it.data == 'T' }
    val initialState = GridState2(personStart, stoneStart).encode()
    // Inspecting the problem input reveals that the final package push must be to the right.
    val targetState = GridState2(target.copy(x = target.x - 1), target).encode()
    graph.connectWithRule {
        TODO() // All the complexity of correctly connecting each state with their neighbouring states
    }
    graph.bfs(initialState, targetState)
    println(graph.distanceTo(targetState))
    TODO() // extra functionality for visualizing
}*/