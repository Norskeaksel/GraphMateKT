package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.graphClasses.Graph
import graphMateKT.graphics.desktopGui.Algorithms
import graphMateKT.graphics.graphGraphics.visualizeGraph
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

internal fun handleVizualizeGraph(
    graphInput: TextArea,
    algorithmSelector: ComboBox<Algorithms>,
    startNode: TextField,
    targetNode: TextField
) {
    println("Reading Graph input")
    val lines = graphInput.text.lines()
    val graph = Graph()
    lines.forEachIndexed { i, line ->
        val uvw = line.trim().split(Regex("\\s+"))
        when (uvw.size) {
            1 -> graph.addNode(uvw[0]).also { println("Adding node: ${uvw[0]}") }
            2 -> graph.addEdge(uvw[0], uvw[1]).also { println("Adding edge from ${uvw[0]} to ${uvw[1]}") }
            // @formatter:off
            3 -> graph.addEdge( uvw[0], uvw[1], uvw[2].toDoubleOrNull() ?: error("Edge weights must be numbers, not '${uvw[2]}'"))
                .also { println("Adding edge from ${uvw[0]} to ${uvw[1]} with weight ${uvw[2].toDouble() }")}
            // @formatter:on
            else -> error("Each line must contain between 1 and 3 strings.")
        }
    }
    println("Graph building complete")
    val start = startNode.text.trim()
    val target = targetNode.text.trim().let { if (it == "") null else it }
    when (algorithmSelector.value) {
        Algorithms.BFS -> graph.bfs(start, target)
        Algorithms.DFS -> graph.dfs(start)
        Algorithms.Dijkstra -> graph.dijkstra(start, target)
        Algorithms.StronglyConnectedComponents -> graph.stronglyConnectedComponents()
        Algorithms.TopologicalSort -> graph.topologicalSort()
        else -> { /* Do nothing */
        }
    }
    graph.visualizeGraph()
}