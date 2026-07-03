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
    isBidirectional: Boolean,
) {
    println("Reading IntGraph input")
    val lines = graphInput.text.lines()
    val (n, m) = lines.first().split(" ").map { it.toInt() }
    val inputLines = lines.drop(1)
    require(inputLines.all { it.split(" ").size != 1 }) { "IntGraphs cannot add a singular node. The nodes have already been defined to be from 0 to ${n - 1}." }
    val edgeMultiplier = if (!isBidirectional) 1 else 2
    require(inputLines.size * edgeMultiplier <= m) { "Can't add a ${m + 1}th edge, becaues it exceedes nrOfEdges=$m." }
    val intGraph = IntGraph(n, m * edgeMultiplier)
    val tempGraph = buildGraph(inputLines, isBidirectional)
    val connections = mutableSetOf<Pair<Int, Int>>()
    tempGraph.nodes().forEach { node ->
        tempGraph.edges(node).forEach { edge ->
            val u = node.toString().toInt()
            val v = edge.second.toString().toInt()
            val connection = min(u, v) to max(v, u)
            if (connection in connections && isBidirectional)
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
            intGraph.visualizeGraph(isBidirectional = isBidirectional, finalPath = order)
            return
        }

        else -> {}
    }
    intGraph.visualizeGraph(isBidirectional = isBidirectional)
}
