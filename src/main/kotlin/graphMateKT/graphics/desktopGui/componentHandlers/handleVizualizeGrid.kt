package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.Tile
import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.desktopGui.Algorithms
import graphMateKT.graphics.gridGraphics.visualizeGrid
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

internal fun handleVizualizeGrid(
    gridInput: TextArea,
    algorithmSelector: ComboBox<Algorithms>,
    startNode: TextField,
    targetNode: TextField,
    wallNode: TextField,
) {
    val lines = gridInput.text.lines()
    require(lines.all { it.length == lines[0].length }) { "All grid lines must have the same length, unless constructed with a width and height." }
    val grid = Grid(lines)
    val nodes = grid.nodes()
    grid.deleteNodesWithData(wallNode.text.trim().singleOrNull() ?: ' ')
    grid.connectGridDefault()

    val starts = nodes.filter { it.data == startNode.text.trim().singleOrNull() }
    val start = starts.firstOrNull() ?: Tile(-1, -1)
    val target = nodes.find { it.data == (targetNode.text.trim().singleOrNull()) }
    when (algorithmSelector.value) {
        Algorithms.BFS -> grid.bfs(starts, target)
        Algorithms.DFS -> grid.dfs(start)
        Algorithms.Dijkstra -> grid.dijkstra(start, target)
        else -> {}
    }
    when (algorithmSelector.value) {
        Algorithms.StronglyConnectedComponents -> run {
            val (nodeVisitationOrder, nodeDistances) = grid.stronglyConnectedComponents().reversed()
                .flatMapIndexed { i, component ->
                    grid.dfs(component.first())
                    grid.currentVisitedNodes().map { it to i.toDouble() }
                }.unzip()
            grid.visualizeGrid(currentVisitedNodes = nodeVisitationOrder, nodeDistances = nodeDistances)
        }

        Algorithms.TopologicalSort -> grid.topologicalSort().let { order -> grid.visualizeGrid(finalPath = order) }
        else -> grid.visualizeGrid()
    }
}