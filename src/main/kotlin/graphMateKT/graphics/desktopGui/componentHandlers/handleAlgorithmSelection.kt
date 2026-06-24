package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.graphics.desktopGui.Algorithms
import javafx.scene.control.ComboBox
import javafx.scene.control.Label


internal fun handleAlgorithmSelection(
    algorithmSelector: ComboBox<Algorithms>,
    algorithmInfo: Label,
) {
    algorithmInfo.tooltip = toolTip(
        when (algorithmSelector.value!!) {
            Algorithms.BFS -> "Performs a Breadth-First Search, which finds the shortest path from the starting node(s) to all other nodes\nassuming the graph is unweighted (all edges have a weight of 1.0)."
            Algorithms.DFS -> "Performs a Depth-First Search on the graph which finds all nodes that's reachable from the starting node."
            Algorithms.Dijkstra -> "Performs Dijkstra's algorithm, which finds the shortest path from the starting node to all other nodes."
            Algorithms.TopologicalSort -> "Builds an order of nodes so that the first nodes has no outgoing edges,\nthen nodes with edges pointing to these and so on, assuming the graph is a Directed Acyclic Graph (DAG).\nThis is done by running a DFS from each node and ordering the nodes by descending depth (post-order)."
            Algorithms.StronglyConnectedComponents -> "Identifies groups where each node is reachable from every other node in the group."
            Algorithms.None -> "Visualize the graph without running any algorithm."
        }
    )
}
