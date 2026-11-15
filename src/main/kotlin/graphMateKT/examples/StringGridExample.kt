package graphMateKT.examples

import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.gridGraphics.visualizeGrid

/** Example showing how to create a [Grid] from a list of strings, where each character becomes a tile's data. */
fun exampleGrid2(): Grid {
    val stringList = listOf(
        "S#X",
        "1##",
        "23E"
    )
    val stringGrid = Grid(stringList)
    stringGrid.deleteNodesWithData('#')
    stringGrid.connectGridDefault()
    val startNode = stringGrid.firstNodeWithData('S')
    val target = stringGrid.firstNodeWithData('E')
    stringGrid.bfs(startNode, target)
    return stringGrid
}