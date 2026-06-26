package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.graphClasses.Graph

internal fun buildGraph(lines: List<String>, bidirectional: Boolean): Graph {
    val graph = Graph()
    lines.forEach { line ->
        val uvw = line.trim().split(Regex("\\s+"))
        if (!bidirectional) {
            when (uvw.size) {
                1 -> graph.addNode(uvw[0]).also { println("Adding node: ${uvw[0]}") }
                2 -> graph.addEdge(uvw[0], uvw[1]).also { println("Adding edge from ${uvw[0]} to ${uvw[1]}") }
                3 -> graph.addEdge(uvw[0], uvw[1], uvw[2].toDouble())
                    .also { println("Adding edge from ${uvw[0]} to ${uvw[1]} with weight ${uvw[2].toDouble()}") }

                else -> error("Each line must contain between 1 and 3 strings.")
            }
        } else {
            when (uvw.size) {
                1 -> graph.addNode(uvw[0]).also { println("Adding node: ${uvw[0]}") }
                2 -> graph.connect(uvw[0], uvw[1]).also { println("Connecting ${uvw[0]} and ${uvw[1]}") }
                3 -> graph.connect(uvw[0], uvw[1], uvw[2].toDouble())
                    .also { println("Connecting ${uvw[0]} and ${uvw[1]} with weight ${uvw[2].toDouble()}") }

                else -> error("Each line must contain between 1 and 3 strings.")
            }
        }
    }
    return graph
}
