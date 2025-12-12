package graphMateKT.examples

import graphMateKT.Tile
import graphMateKT.graphClasses.Grid
import graphMateKT.gridGraphics.visualizeGrid
import kotlin.math.sin
import kotlin.system.measureNanoTime

val currentVistedNodes: MutableList<Tile> = mutableListOf()

internal fun main() {
    val ans = Day7Laboratories2025()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/Day7Laboratories2025 */
internal fun Day7Laboratories2025(): Long {
    val input = generateSequence { readlnOrNull() }
    val g = Grid(input.toList())
    val sink = Tile(0,0, '^')
    g.addNode(sink)
    g.connectGrid { t ->
        if (t.data == '^')
            emptyList()
        else {
            val bottomTiles = g.getAllNeighbours(t).filter { it.y > t.y }
            if (bottomTiles.any { it.data == '^' && it.x == t.x }) {
                bottomTiles
            } else {
                bottomTiles.filter { it.x == t.x }
            }
        }
    }
    val startNode = g.nodes().first { it.data == 'S' }
    g.bfs(startNode)
    val splittersHit = g.currentVisitedNodes().filter { it.data == '^' }
    // g.visualizeGrid(finalPath = splittersHit, screenWidthOverride = 1400.0, startPaused = true, screenTitle = "Day7 Laboratories - BFS Splitter Counter Visualization with GraphMateKT")
    // return splittersHit.size
    val maxDistance = g.visitedNodes().maxOf { g.distanceTo(it) }
    val endNodes = g.nodes().filter { g.distanceTo(it) == maxDistance }
    endNodes.forEach { endNode ->
        g.addEdge(endNode, sink)
    }
    g.resetSearchResults()
    val nrOfPaths = g.countPaths(startNode, sink)
    // g.visualizeGrid(currentVisitedNodes = currentVistedNodes)
    return nrOfPaths
}

private fun Grid.countPaths(startNode: Tile, sinkNode:Tile, memo: MutableMap<Tile, Long> = mutableMapOf()): Long {
    if (startNode == sinkNode)
        return 1
    if (memo.containsKey(startNode))
        return memo[startNode]!!
    if(currentVistedNodes.contains(startNode))
        return 0 // Avoid cycles
    currentVistedNodes.add(startNode)
    var pathNr = 0L
    neighbours(startNode).forEach { neighbor ->
        pathNr +=  countPaths(neighbor, sinkNode, memo)
    }
    memo[startNode] = pathNr
    return pathNr
}