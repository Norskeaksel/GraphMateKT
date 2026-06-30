package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.graphics.desktopGui.Algorithms
import graphMateKT.graphics.graphGraphics.visualizeComponents
import graphMateKT.graphics.graphGraphics.visualizeGraph
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

internal fun handleVizualizeGraph(
    graphInput: TextArea,
    algorithmSelector: ComboBox<Algorithms>,
    startNode: TextField,
    targetNode: TextField,
    isBidirectional: Boolean,
) {
    println("Reading Graph input")
    val lines = graphInput.text.lines()
    val graph = buildGraph(lines, isBidirectional)
    val start = startNode.text.trim()
    val target = targetNode.text.trim()
    when (algorithmSelector.value) {
        Algorithms.BFS -> graph.bfs(start, target)
        Algorithms.DFS -> graph.dfs(start)
        Algorithms.Dijkstra -> graph.dijkstra(start, target)
        Algorithms.StronglyConnectedComponents -> graph.stronglyConnectedComponents().run {
            visualizeComponents()
            return
        }

        Algorithms.TopologicalSort -> graph.topologicalSort().let { order ->
            graph.visualizeGraph(isBidirectional = isBidirectional, finalPath = order)
            return
        }

        else -> {}
    }
    graph.visualizeGraph(isBidirectional = isBidirectional)
}
