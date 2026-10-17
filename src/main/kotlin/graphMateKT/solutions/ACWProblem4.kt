package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.graphClasses.Grid
import graphMateKT.graphClasses.IntGraph
import graphMateKT.graphics.gridGraphics.visualizeGrid
import java.io.InputStream
import kotlin.math.pow

internal fun main() {
    val ans = ACWProblem4(System.`in`)
    println(ans)
    System.out.flush()
}

var base = 150

private data class GridState(val person: Tile, val stone: Tile) {
    fun encode() = ((person.x * base + person.y) * base + stone.x) * base + stone.y

    companion object {
        fun decode(code: Int): GridState {
            val y2 = code % base
            val x2 = code / base % base
            val y1 = code / (base * base) % base
            val x1 = code / (base * base * base)
            return GridState(Tile(x1, y1, '.'), Tile(x2, y2, '.'))
        }
    }
}

private fun solve(grid: Grid): Triple<Double, List<Int>, List<Int>> {
    val personStart = grid.nodes().first { it.data == 'X' }.copy(data = '.')
    val stoneStart = grid.nodes().first { it.data == '*' }.copy(data = '.')
    val target = grid.nodes().first { it.data == 'T' }.copy(data = '.')
    grid.addNode(personStart)
    grid.addNode(stoneStart)
    grid.addNode(target)

    val nrOfStates = base.toDouble().pow(4).toInt()
    val graph = IntGraph(nrOfStates, true)

    val initialState = GridState(personStart, stoneStart).encode()
    val targetState = GridState(target.copy(x = target.x - 1), target).encode()
    graph.connectWithRule { stateCode ->
        val nextStates = mutableListOf<GridState>()
        val gridState = GridState.decode(stateCode)
        gridState.run {
            val personNeighbors = grid.getStraightNeighbours(person)
            val stonelessNeighbours = personNeighbors.filter { it != stone }
            nextStates.addAll(stonelessNeighbours.map { GridState(it, stone) })
            if (personNeighbors.contains(stone)) {
                val nextStoneState = if (person.y == stone.y - 1) {
                    grid.xy2NodeOrNull(stone.x, stone.y + 1)
                } else if (person.x == stone.x - 1) {
                    grid.xy2NodeOrNull(stone.x + 1, stone.y)
                } else if (person.x == stone.x + 1) {
                    grid.xy2NodeOrNull(stone.x - 1, stone.y)
                } else if (person.y == stone.y + 1) {
                    grid.xy2NodeOrNull(stone.x, stone.y - 1)
                } else null
                nextStoneState?.let {
                    nextStates.add(GridState(stone, it))
                }
            }
        }
        nextStates.filter { it.person.y <= it.stone.y || it.person.y <= gridState.person.y }.map { it.encode() }
    }
    graph.bfs(initialState, targetState)
    return Triple(graph.distanceTo(targetState), graph.currentVisitedNodes(), graph.getPath(targetState)!!)
}

/** Solves https://open.kattis.com/problems/AWCProblem4 */
internal fun ACWProblem4(inputStream: InputStream): Int {
    val scanner = InputReader(inputStream)
    val lines = generateSequence { scanner.nextString() }.toList()
    base = lines.size
    val grid = Grid(lines)
    grid.deleteNodesWithData('#')
    println("Finding Solution")
    val (distance, visitedNodeCodes, finalPathCodes) = solve(grid)
    println("Remapping nodes")
    val personStatesSet = mutableSetOf<Tile>()
    val stoneStatesSet = mutableSetOf<Tile>()
    val finalStates = mutableListOf<Pair<Tile, Double>>()
    val boost = visitedNodeCodes.size * 2.5

    visitedNodeCodes.forEachIndexed { i, it ->
        val (personState, stoneState) = GridState.decode(it)
        if (stoneState !in stoneStatesSet)
            finalStates.add(stoneState to i.toDouble() + boost)
        stoneStatesSet.add(stoneState)
        if (personState !in personStatesSet)
            finalStates.add(personState to i.toDouble())
        personStatesSet.add(personState)
    }
    val (visitedNodes, distances) = finalStates.unzip()
    val finalPath = finalPathCodes.map { GridState.decode(it).person }
    println("Visualizing Solution")
    grid.visualizeGrid(
        visitedNodes,
        finalPath,
        distances,
        "Autumn Code Walk final problem. Ways to push a stone.",
        0.40,
        startPaused = true,
        screenWidthMultiplier = 1.8
    )
    return distance.toInt()
}
