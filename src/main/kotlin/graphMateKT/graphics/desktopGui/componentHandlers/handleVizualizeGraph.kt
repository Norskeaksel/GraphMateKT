package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.graphClasses.Graph
import graphMateKT.graphics.desktopGui.Algorithms
import graphMateKT.graphics.graphGraphics.visualizeGraph
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

fun handleVizualizeGraph(
    graphInput: TextArea,
    algorithmSelector: ComboBox<Algorithms>,
    startNode: TextField,
    targetNode: TextField
) {
    println("Reading graph input")
    val lines = graphInput.text.lines()
    val graph = Graph()
    lines.forEachIndexed { i, line ->
        val uvw = line.trim().split(Regex("\\s+"))
        when (uvw.size) {
            1 -> graph.addNode(uvw[0]).also { println("Adding node: ${uvw[0]}") }
            2 -> graph.addEdge(uvw[0], uvw[1]).also { println("Adding edge from ${uvw[0]} to ${uvw[1]}") }
            // @formatter:off
            3 -> graph.addEdge( uvw[0], uvw[1], uvw[2].toDoubleOrNull() ?: run { System.err.println("Invalid weight '${uvw[2]}'. Defaulting to 1.0"); 1.0 })
                .also { println("Adding edge from ${uvw[0]} to ${uvw[1]} with weight ${uvw[2].toDoubleOrNull() ?: 1.0}") }
            // @formatter:on
            else -> System.err.println("Ignoring invalid input on line ${i + 1}")
        }
    }
    println("Graph building complete")
    val starts = startNode.text.split(",").map { it.trim() }
    val start = starts[0].trim()
    val target = targetNode.text.trim().let { if (it == "") null else it }
    when (algorithmSelector.value) {
        Algorithms.BFS -> if (starts.size > 1) graph.bfs(starts, target) else graph.bfs(start, target)
        Algorithms.DFS -> graph.dfs(start)
        Algorithms.Dijkstra -> graph.dijkstra(start, target)
        Algorithms.StronglyConnectedComponents -> graph.stronglyConnectedComponents()
        Algorithms.TopologicalSort -> graph.topologicalSort()
        else -> { /* Do nothing */
        }
    }
    graph.visualizeGraph()
}