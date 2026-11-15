package graphMateKT.examples

import graphMateKT.graphClasses.IntGraph


/** Example showing how to build a weighted, directed [IntGraph], find shortest paths with Dijkstra and visualize the result.
 *
 * An IntGraph needs to be initialized with a fixed size. It will consist of nodes from 0 to size-1.
 *
 * NOTE: To visualize intGraphs, the files `smartgraph.css` and `smartgraph.properties` must be present in the root of
 * the project. They can be found here: https://github.com/Norskeaksel/GraphMateKT
 * @return An example IntGraph */
fun exampleIntGraph(): IntGraph {
    // --- Example IntGraph Definition ---
    val n = 6
    val intGraph = IntGraph(n)
    intGraph.addEdge(0, 1, 10.0)
    intGraph.addEdge(0, 2, 3.0)
    intGraph.addEdge(1, 3, 2.0)
    intGraph.addEdge(2, 1, 4.0)
    intGraph.addEdge(2, 3, 8.0)
    intGraph.addEdge(2, 4, 2.0)
    intGraph.addEdge(3, 4, 5.0)
    // Node 5 is isolated, since all nodes from 0 to n-1 exist in an IntGraph

    val startNode = 0
    val targetNode = 3
    intGraph.shortestPathsFrom(
        startNode,
        targetNode
    ) // Specifying a targetNode enables visualizeGraph() to show the finalPath()
    val intNodes: List<Int> = intGraph.nodes()
    println("Shortest paths from source node $startNode:")
    intNodes.forEach { node ->
        val distValue = intGraph.distanceTo(node)
        val path = intGraph.pathTo(node)
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
    println("Final path to target node $targetNode: ${intGraph.finalPath()}")

    /* To visualize the graph using brunomnsilva's JavaFXSmartGraph: https://github.com/brunomnsilva/JavaFXSmartGraph, run:
    intGraph.visualizeGraph(
        screenTitle = "Visualizing Dijkstra's shortest path with GraphMateKT",
        animationTicTimeOverride = 1000.0,
        startPaused = true,
    )*/
    return intGraph
}

