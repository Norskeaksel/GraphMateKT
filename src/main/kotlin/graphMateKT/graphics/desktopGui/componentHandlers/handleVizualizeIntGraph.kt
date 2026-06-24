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
        val u = uvw[0].toInt()
        val v = uvw[1].toInt()
        when (uvw.size) {
            2 -> graph.addEdge(u, v).also { println("Adding edge from $u to $v") }
            3 -> graph.addEdge(u, v, uvw[2].toDouble())
                .also { println("Adding edge from $u to $v with weight ${uvw[2].toDouble()}") }

            else -> error("Each IntGraph input row must contain either 2 or 3 numbers.")
        }
    }
    val start = startNode.text.trim().toIntOrNull()
    val target = targetNode.text.trim().toIntOrNull()
    when (algorithmSelector.value) {
        Algorithms.BFS -> start?.let { graph.bfs(it, target) } ?: error("Start node must be an integer.")
        Algorithms.DFS -> start?.let { graph.dfs(it) } ?: error("Start node must be an integer.")
        Algorithms.Dijkstra -> start?.let { graph.dijkstra(start, target) } ?: error("Start node must be an integer.")
        Algorithms.StronglyConnectedComponents -> graph.stronglyConnectedComponents()
        Algorithms.TopologicalSort -> graph.topologicalSort()
        else -> { /* Do nothing */
        }
    }
    graph.visualizeGraph()
}