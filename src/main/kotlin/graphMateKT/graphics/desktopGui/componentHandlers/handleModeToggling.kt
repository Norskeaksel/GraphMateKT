package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.graphics.desktopGui.GUIConstants
import javafx.scene.control.*

internal fun handleModeToggling(
    graphInput: TextArea,
    modeBtns: Triple<ToggleButton, ToggleButton, ToggleButton>,
    radioBtnRow: List<Labeled>,
    wallRow: List<Control>,
    infoText: InfoText,
    vizualizeBtn: Button,
) {
    val (graphBtn, gridBtn, intGraphBtn) = modeBtns
    graphInput.style = if (gridBtn.isSelected) "-fx-font-family: Monospace" else ""
    infoText.gridMode = gridBtn.isSelected
    if (graphBtn.isSelected) {
        graphInput.text = GUIConstants.GRAPH_INPUT
        infoText.graphInfo = GUIConstants.GRAPH_INFO
        vizualizeBtn.text = graphBtn.text
    }
    if (gridBtn.isSelected) {
        graphInput.text = GUIConstants.GRID_INPUT
        infoText.graphInfo = GUIConstants.GRID_INPUT_INFO
        vizualizeBtn.text = gridBtn.text
        wallRow.forEach { it.isVisible = true; it.isManaged = true }
        radioBtnRow.forEach { it.isVisible = false; it.isManaged = false}
    }
    else{
        wallRow.forEach { it.isVisible = false; it.isManaged = false }
        radioBtnRow.forEach { it.isVisible = true; it.isManaged = true}
    }
    if (intGraphBtn.isSelected) {
        graphInput.text = GUIConstants.INT_GRAPH_INPUT
        infoText.graphInfo = GUIConstants.INT_GRAPH_INPUT_INFO
        vizualizeBtn.text = intGraphBtn.text
    }
}
