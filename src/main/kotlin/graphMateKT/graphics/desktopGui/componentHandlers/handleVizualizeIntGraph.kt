package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.graphClasses.IntGraph
import graphMateKT.graphics.desktopGui.Algorithms
import graphMateKT.graphics.graphGraphics.visualizeGraph
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

internal fun handleVizualizeIntGraph(
    graphInput: TextArea,
    algorithmSelector: ComboBox<Algorithms>,
    startNode: TextField,
    targetNode: TextField
) {
    println("Reading IntGraph input")
    val lines = graphInput.text.lines()
    val (n, m) = lines.first().split(" ").map { it.toInt() }
    val graph = IntGraph(n, m)
    lines.forEachIndexed { i, line ->
        if (i == 0) return@forEachIndexed
        val uvw = line.trim().split(Regex("\\s+"))
        // @formatter:off
        if (uvw.size !in 2..3)    run { System.err.println("Ignoring invalid input on line ${i + 1}"); return@forEachIndexed }
        val u = uvw[0].toIntOrNull() ?: run { System.err.println("Ignoring invalid input on line ${i + 1}"); return@forEachIndexed }
        val v = uvw[1].toIntOrNull() ?: run { System.err.println("Ignoring invalid input on line ${i + 1}"); return@forEachIndexed }

        when (uvw.size) {
            2 -> graph.addEdge(u, v).also { println("Adding edge from $u to $v") }
            3 -> graph.addEdge( u, v, uvw[2].toDoubleOrNull() ?: run { System.err.println("Invalid weight '${uvw[2]}'. Defaulting to 1.0"); 1.0 })
                .also { println("Adding edge from $u to $v with weight ${uvw[2].toDoubleOrNull() ?: 1.0}") }
            // @formatter:on
            else -> error("Each graph input row m")
        }
    }
    println("Graph building complete")
    val starts = startNode.text.split(",").map { it.trim().toInt() }
    val start = starts[0]
    val target = targetNode.text.trim().let { if (it == "") null else it.toInt() }
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