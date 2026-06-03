package graphMateKT.examples

import graphMateKT.graphClasses.Graph
import graphMateKT.graphClasses.IntGraph
import graphMateKT.graphGraphics.visualizeGraph


internal fun main() {
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
    val targetNode = 3
    graph.dijkstra(startNode, targetNode) // Provide a goal target node to stop the search when the target is found
    val nodes: List<Int> =
        graph.nodes().map { it as Int } // Nodes are of type Any and must therefore be cast to Int
    println("Shortest paths from source node $startNode:")
    nodes.forEach { node ->
        val distValue = graph.distanceTo(node)
        val path = graph.getPath(node)
        println("To node $node: Distance $distValue Path: ${if (distValue < Int.MAX_VALUE) path else null}")
    }
    /* Output:
        Shortest paths from source node 0:
        Distance to node 0: 0.0 Path: [0]
        Distance to node 1: 7.0 Path: [0, 2, 1]
        Distance to node 2: 3.0 Path: [0, 2]
        Distance to node 3: 9.0 Path: [0, 2, 1, 3]
        Distance to node 4: 5.0 Path: [0, 2, 4]
        Distance to node 5: Infinity Path: null
    */


    /* --- Example IntGraph Definition ---
         * An IntGraph can needs to be initialized with a fixed size, because it will consist of integer nodes from
         0 to size-1.
    */

    val n = graph.size()
    val intGraph = IntGraph(n)
    // Add the same edges as the above Graph
    graph.nodes().forEach { fromNode ->
        graph.forEachEdge(fromNode) { edge ->
            val weight = edge.first
            val toNode = edge.second as Int // Cast type Any to Int
            intGraph.addEdge(fromNode as Int, toNode, weight)
        }
    }
    intGraph.dijkstra(startNode, targetNode)
    val intNodes: List<Int> = intGraph.nodes()
    println("Shortest paths from source node $startNode:")
    intNodes.forEach { node ->
        val distValue = intGraph.distanceTo(node)
        val path = intGraph.getPath(node)
        println("To node $node: Distance $distValue Path: ${if (distValue < Int.MAX_VALUE) path else null}")
    }
    // Outputs the same as the code above

    // Visualize the graph using brunomnsilva's JavaFXSmartGraph: https://github.com/brunomnsilva/JavaFXSmartGraph
    graph.visualizeGraph(
        // Also works with intGraph.visualizeSearch(
        screenTitle = "Visualizing Dijkstra's shortest path with GraphMateKT",
        animationTicTimeOverride = 1000.0,
        startPaused = true,
    )
}
