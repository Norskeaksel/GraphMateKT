package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.graphics.desktopGui.GUIConstants
import javafx.scene.control.*

internal fun handleModeToggling(
    graphInput: TextArea,
    modeBtns: Triple<ToggleButton, ToggleButton, ToggleButton>,
    radioBtnRow: List<Labeled>,
    wallRow: List<Control>,
) {
    val (graphBtn, gridBtn, intGraphBtn) = modeBtns
    graphInput.style = if (gridBtn.isSelected) "-fx-font-family: Monospace" else ""
    if (graphBtn.isSelected) {
        graphInput.text = GUIConstants.GRAPH_INPUT
    }
    if (gridBtn.isSelected) {
        graphInput.text = GUIConstants.GRID_INPUT
        wallRow.forEach { it.isVisible = true; it.isManaged = true }
        radioBtnRow.forEach { it.isVisible = false; it.isManaged = false}
    }
    else{
        wallRow.forEach { it.isVisible = false; it.isManaged = false }
        radioBtnRow.forEach { it.isVisible = true; it.isManaged = true}
    }
    if (intGraphBtn.isSelected) {
        graphInput.text = GUIConstants.INT_GRAPH_INPUT
    }
}
