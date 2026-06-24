package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.Tile
import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.desktopGui.Algorithms
import graphMateKT.graphics.gridGraphics.visualizeGrid
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

internal fun handleVizualizeGrid(
    gridInput: TextArea, algorithmSelector: ComboBox<Algorithms>, startNode: TextField, targetNode: TextField
) {
    val lines = gridInput.text.lines()
    val firstLine = lines[0].trim()
    val startData = startNode.text.trim()
    var start: Tile = Tile(0, 0)
    var starts = listOf(start)
    var target: Tile? = null
    val grid = if (lines.size == 1 && firstLine.split(Regex("\\s+")).size == 2) {
        val (width, height) = lines[0].trim().split(Regex("\\s+")).map { it.toInt() }
        Grid(width, height)
    } else if (lines.any { it.length == lines[0].length }) {
        Grid(lines)
    } else {
        error("Invalid grid input.")
    }
    // TODO handle walls starts and targets
    grid.connectGridDefault()
    when (algorithmSelector.value) {
        Algorithms.BFS -> grid.bfs(start, target)
        Algorithms.DFS -> grid.dfs(start)
        Algorithms.Dijkstra -> grid.dijkstra(start, target)
        Algorithms.StronglyConnectedComponents -> grid.stronglyConnectedComponents()
        Algorithms.TopologicalSort -> grid.topologicalSort()
        else -> { /* Do nothing */
        }
    }
    grid.visualizeGrid()
}