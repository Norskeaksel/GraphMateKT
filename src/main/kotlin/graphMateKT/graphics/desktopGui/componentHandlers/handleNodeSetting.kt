package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.graphics.desktopGui.Algorithms
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup


fun handleNodeSetting(
    visualisationMode: ToggleGroup,
    algorithmSelector: ComboBox<Algorithms>,
    startNode: TextField,
    targetNode: TextField,
    startLabel: Label,
    targetLabel: Label
) {
    startLabel.text = if (algorithmSelector.value == Algorithms.BFS) "Start node(s):" else "Start node:"
    when (algorithmSelector.value) {
        Algorithms.BFS -> run { startNode.text = "0,5"; targetNode.text = "3" }
        Algorithms.Dijkstra -> run { startNode.text = "0"; targetNode.text = "3" }
        Algorithms.DFS -> run { startNode.text = "0"; targetNode.text = "" }
        else -> run { startNode.text = ""; targetNode.text = "" }
    }
    startNode.isDisable = when (algorithmSelector.value) {
        Algorithms.BFS, Algorithms.DFS, Algorithms.Dijkstra -> false
        else -> true
    }
}
