package graphMateKT.graphics.desktopGui.buttonHandlers

import graphMateKT.graphClasses.Graph
import graphMateKT.graphics.graphGraphics.visualizeGraph
import javafx.scene.control.TextArea

fun handleVizualizeGraph(graphInput: TextArea) {
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
    graph.visualizeGraph()
}