package graphMateKT.examples

import graphMateKT.graphClasses.Grid
import graphMateKT.Tile
import graphMateKT.graphics.gridGraphics.visualizeGrid

internal fun main() {
    // Example Grid Definition. We can also initialize it with a list of strings
    val width = 99
    val height = 99
    val grid = Grid(width, height)

    // We can delete nodes, by specifying them, their coordinates or their data. However, deletions MUST take place
    // before connections are added. Otherwise, the grid can contain connections to the deleted tiles
    // Let's use some custom functions to delete some patterns

    grid.deleteSquareAtOffset(4)
    grid.deleteDiamondAtOffset(8)
    grid.deleteSquareAtOffset(10)
    grid.deleteDiamondAtOffset(20)
    grid.deleteSquareAtOffset(22)
    grid.deleteDiamondAtOffset(44)
    grid.deleteSquareAtOffset(46)

    // We could use `grid.connectGridDefault()` to connect all nodes, but let's define a custom connection instead.
    fun connectDownOrRight(t: Tile): List<Tile> = grid.getStraightNeighbours(t).filter { it.x >= t.x || it.y > t.y }
    grid.connectGrid(bidirectional = true, ::connectDownOrRight)

    // Nodes in a grid consists of Tile objects with x, y coordinates and data
    val startNode = Tile(width / 2, height / 2)

    // We can run a seach algorithm like BFS (Breadth-First Search) from a start node
    val target = Tile(width - 1, height - 1) // Define a target to find a path to it
    grid.bfs(startNode, target)

    // Visualizing the grid, the BFS and the final fastest path to the target
    grid.visualizeGrid(
        screenTitle = "Breadth-First Search from the center to the bottom right corner, using GraphMateKT",
        screenWidthOverride = 880.0,
        startPaused = true,
    )
}

private fun Grid.deleteSquareAtOffset(centerOffset: Int) {
    val center = width / 2
    val lowerBound = center - centerOffset
    val upperBound = center + centerOffset
    for (i in lowerBound + 2 until upperBound - 1) {
        deleteNodeAtXY(i, lowerBound)
        deleteNodeAtXY(i, upperBound)
        deleteNodeAtXY(lowerBound, i)
        deleteNodeAtXY(upperBound, i)
    }
}

private fun Grid.deleteDiamondAtOffset(centerOffset: Int) {
    val center = width / 2
    var dx = centerOffset
    var dy = 1
    repeat(centerOffset) {
        deleteNodeAtXY(center - dx, center - dy)
        deleteNodeAtXY(center + dx, center - dy)
        deleteNodeAtXY(center - dx, center + dy)
        deleteNodeAtXY(center + dx, center + dy)
        dx--
        dy++
    }
}
