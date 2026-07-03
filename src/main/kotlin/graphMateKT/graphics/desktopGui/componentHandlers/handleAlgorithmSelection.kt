package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.graphics.desktopGui.Algorithms
import graphMateKT.graphics.desktopGui.GUIConstants
import javafx.scene.control.ComboBox


internal fun handleAlgorithmSelection(
    algorithmSelector: ComboBox<Algorithms>,
    infoText: InfoText,
) {
    infoText.algorithmInfo = when (algorithmSelector.value) {
        Algorithms.BFS -> "Perform a Breadth-First Search, which finds the shortest path from the starting node(s) to all other nodes, assuming the graph is unweighted (all edges have a weight of 1.0)."
        Algorithms.DFS -> "Perform a Depth-First Search on the graph which finds all nodes that's reachable from the starting node."
        Algorithms.Dijkstra -> "Perform Dijkstra's algorithm, which finds the shortest path from the starting node to all other nodes."
        Algorithms.TopologicalSort -> "Build an order of nodes so that the first nodes has no outgoing edges, then nodes with edges pointing to these and so on, assuming the graph is a Directed Acyclic Graph (DAG). This is done by running a DFS from each node and ordering the nodes by descending depth (post-order)."
        Algorithms.StronglyConnectedComponents -> "Identify groups where each node is reachable from every other node in the group."
        else -> GUIConstants.ALGORITHM_INFO
    }
}
