package graphMateKT.examples

import graphMateKT.Tile
import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.gridGraphics.visualizeGrid

internal fun main() {
    val grid = Grid(2, 2)
    grid.visualizeGrid(
        currentVisitedNodes = listOf(Tile(0, 0), Tile(1, 0), Tile(1, 1), Tile(0, 1)),
        finalPath = listOf(Tile(0, 0), Tile(1, 0), Tile(1, 1)),
        nodeDistances = listOf(0.0, 1.0, 2.0, 3.0),
        screenTitle = "GraphMateKT visualizeGrid example usage",
        animationTicTimeOverride = 1000.0,
        closeOnEnd = true,
        startPaused = true,
        screenWidthMultiplier = 0.3
    )
    val stringList = listOf(
        "S#X",
        "1##",
        "23E"
    )
    val stringGrid = Grid(stringList)
    stringGrid.deleteNodesWithData('#')
    stringGrid.connectGridDefault()
    val startNode = Tile(0, 0)
    val target = Tile(2, 2)
    stringGrid.bfs(startNode, target)
    stringGrid.visualizeGrid()
}