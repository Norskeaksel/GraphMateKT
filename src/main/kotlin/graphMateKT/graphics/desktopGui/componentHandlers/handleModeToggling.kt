package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.graphics.desktopGui.GUIConstants
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import javafx.scene.text.Font
import javafx.util.Duration

internal fun toolTip(text: String) = Tooltip(text).apply {
        font = Font.font(GUIConstants.guiFontSize)
        showDelay = Duration.ZERO
        showDuration = Duration.minutes(1.0)
    }

internal fun handleModeToggling(
    graphInput: TextArea,
    modeBtns: Triple<ToggleButton, ToggleButton, ToggleButton>,
    inputLabel: Label,
    inputInfoIcon: Label,
) {
    val (graphBtn, gridBtn, intGraphBtn) = modeBtns
    graphInput.style = if (gridBtn.isSelected) "-fx-font-family: Monospace" else ""
    if (graphBtn.isSelected) {
        graphInput.text = GUIConstants.graphInput
        inputLabel.text = "Graph input:"
        inputInfoIcon.tooltip = toolTip(GUIConstants.graphInputInfo)
    }
    if (gridBtn.isSelected) {
        graphInput.text = GUIConstants.gridInput
        inputLabel.text = "Grid input:"
        inputInfoIcon.tooltip = toolTip(GUIConstants.gridInputInfo)
    }
    if (intGraphBtn.isSelected) {
        graphInput.text = GUIConstants.intGraphInput
        inputLabel.text = "IntGraph input:"
        inputInfoIcon.tooltip = toolTip(GUIConstants.intGraphInputInfo)
    }
}
