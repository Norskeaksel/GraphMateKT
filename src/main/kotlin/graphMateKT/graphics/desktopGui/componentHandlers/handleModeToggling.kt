package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.graphics.desktopGui.ModeText
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip


internal fun handleModeToggling(
    graphInput: TextArea,
    gridBtn: ToggleButton,
    graphBtn: ToggleButton,
    intGraphBtn: ToggleButton,
    inputLabel: Label,
    inputInfoIcon: Label,
) {
    graphInput.style = if (gridBtn.isSelected) "-fx-font-family: Monospace" else ""
    if (graphBtn.isSelected) {
        graphInput.text = ModeText.graphInput
        inputLabel.text = "Graph input"
        inputInfoIcon.tooltip = Tooltip(ModeText.gridInputInfo)
    }
    if (gridBtn.isSelected) {
        graphInput.text = ModeText.gridInput
        inputLabel.text = "Grid input"
        inputInfoIcon.tooltip = Tooltip(ModeText.gridInputInfo)
    }
    if (intGraphBtn.isSelected) {
        graphInput.text = ModeText.intGraphInput
        inputLabel.text = "IntGraph input"
        inputInfoIcon.tooltip = Tooltip(ModeText.intGraphInput)
    }
}
