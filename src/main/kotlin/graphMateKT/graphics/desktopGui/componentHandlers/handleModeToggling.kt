package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.graphics.desktopGui.GUIConstants
import javafx.scene.control.*
import javafx.scene.text.Font
import javafx.util.Duration

internal fun toolTip(text: String) = Tooltip(text).apply {
    font = Font.font(GUIConstants.GUI_FONT_SIZE)
    showDelay = Duration.ZERO
    showDuration = Duration.minutes(1.0)
}

internal fun handleModeToggling(
    graphInput: TextArea,
    modeBtns: Triple<ToggleButton, ToggleButton, ToggleButton>,
    radioBtnRow: List<Labeled>,
    wallRow: List<Control>,
    inputInfoIcon: Label,
    vizualizeGraphBtn: Button,
) {
    val (graphBtn, gridBtn, intGraphBtn) = modeBtns
    graphInput.style = if (gridBtn.isSelected) "-fx-font-family: Monospace" else ""
    if (graphBtn.isSelected) {
        graphInput.text = GUIConstants.GRAPH_INPUT
        inputInfoIcon.tooltip = toolTip(GUIConstants.GRAPH_INFO)
        vizualizeGraphBtn.text = graphBtn.text
    }
    if (gridBtn.isSelected) {
        graphInput.text = GUIConstants.GRID_INPUT
        inputInfoIcon.tooltip = toolTip(GUIConstants.GRID_INPUT_INFO)
        vizualizeGraphBtn.text = graphBtn.text
        wallRow.forEach { it.isVisible = true; it.isManaged = true }
        radioBtnRow.forEach { it.isVisible = false; it.isManaged = false}
    }
    else{
        wallRow.forEach { it.isVisible = false; it.isManaged = false }
        radioBtnRow.forEach { it.isVisible = true; it.isManaged = true}
    }
    if (intGraphBtn.isSelected) {
        graphInput.text = GUIConstants.INT_GRAPH_INPUT
        inputInfoIcon.tooltip = toolTip(GUIConstants.INT_GRAPH_INPUT_INFO)
        vizualizeGraphBtn.text = intGraphBtn.text
    }
}
