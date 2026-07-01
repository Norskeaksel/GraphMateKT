package graphMateKT.examples

import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.gridGraphics.visualizeGrid

internal fun main() {
    val stringList = listOf(
        "S#X",
        "1##",
        "23E"
    )
    val stringGrid = Grid(stringList)
    stringGrid.deleteNodesWithData('#')
    stringGrid.connectGridDefault()
    val startNode = stringGrid.firstNodeWithData("S")
    val target = stringGrid.firstNodeWithData("E")
    stringGrid.bfs(startNode, target)
    stringGrid.visualizeGrid()
}