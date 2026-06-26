package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.graphClasses.IntGraph
import graphMateKT.graphics.desktopGui.Algorithms
import graphMateKT.graphics.graphGraphics.visualizeComponents
import graphMateKT.graphics.graphGraphics.visualizeGraph
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import kotlin.math.max
import kotlin.math.min

internal fun handleVizualizeIntGraph(
    graphInput: TextArea,
    algorithmSelector: ComboBox<Algorithms>,
    startNode: TextField,
    targetNode: TextField,
    bidirectional: Boolean,
) {
    println("Reading IntGraph input")
    val lines = graphInput.text.lines()
    val (n, m) = lines.first().split(" ").map { it.toInt() }
    val intGraph = IntGraph(n, m * if (!bidirectional) 1 else 2)
    val tempGraph = buildGraph(lines.drop(1), bidirectional)
    val connections = mutableSetOf<Pair<Int, Int>>()
    tempGraph.nodes().forEach { node ->
        tempGraph.edges(node).forEach { edge ->
            val u = node.toString().toInt()
            val v = edge.second.toString().toInt()
            val connection = min(u, v) to max(v, u)
            if (connection in connections)
                return@forEach
            intGraph.addEdge(u, v, edge.first)
            connections.add(connection)
        }
    }
    val start = startNode.text.trim().toInt()
    val target = targetNode.text.trim().toInt()
    when (algorithmSelector.value) {
        Algorithms.BFS -> intGraph.bfs(start, target)
        Algorithms.DFS -> intGraph.dfs(start)
        Algorithms.Dijkstra -> intGraph.dijkstra(start, target)
        Algorithms.StronglyConnectedComponents -> intGraph.stronglyConnectedComponents().run {
            visualizeComponents()
            return
        }

        Algorithms.TopologicalSort -> intGraph.topologicalSort().let { order ->
            intGraph.visualizeGraph(bidirectional = bidirectional, finalPath = order)
            return
        }

        else -> {}
    }
    intGraph.visualizeGraph(bidirectional = bidirectional)
}
