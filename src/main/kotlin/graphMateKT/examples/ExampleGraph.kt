package graphMateKT.examples

import graphMateKT.graphClasses.Graph


/** Example showing how to build a weighted, directed graph, find shortest paths with Dijkstra and visualize the result.
 *
 * NOTE: To visualize intGraphs, the files `smartgraph.css` and `smartgraph.properties` must be present in the root of
 * the project. They can be found here: https://github.com/Norskeaksel/GraphMateKT */
fun exampleGraph(): Graph {
    // --- Example Graph Definition ---
    val graph = Graph()
    graph.addEdge(0, 1, 10.0)
    graph.addEdge(0, 2, 3.0)
    graph.addEdge(1, 3, 2.0)
    graph.addEdge(2, 1, 4.0)
    graph.addEdge(2, 3, 8.0)
    graph.addEdge(2, 4, 2.0)
    graph.addEdge(3, 4, 5.0)
    graph.addNode(5) // Adding an isolated node is also possible

    val startNode = 0
    graph.shortestPathsFrom(startNode)
    val nodes: List<Int> =
        graph.nodes().map { it as Int } // Nodes are of type Any and must therefore be cast to Int
    println("Shortest paths from source node $startNode:")
    nodes.forEach { node ->
        val distValue = graph.distanceTo(node)
        val path = graph.pathTo(node)
        println("To node $node: Distance $distValue Path: ${if (distValue < Int.MAX_VALUE) path else null}")
    }
    /* Output:
        Shortest paths from source node 0:
        To node 0: Distance 0.0 Path: [0]
        To node 1: Distance 7.0 Path: [0, 2, 1]
        To node 2: Distance 3.0 Path: [0, 2]
        To node 3: Distance 9.0 Path: [0, 2, 1, 3]
        To node 4: Distance 5.0 Path: [0, 2, 4]
        To node 5: Distance Infinity Path: null
    */
    /* To visualize the graph using brunomnsilva's JavaFXSmartGraph: https://github.com/brunomnsilva/JavaFXSmartGraph, run:
    graph.visualizeGraph(
        screenTitle = "Visualizing Dijkstra's shortest path with GraphMateKT",
        animationTicTimeOverride = 1000.0,
        startPaused = true,
    ) */
    return graph
}
