package graphMateKT.graphics.desktopGui.buttonHandlers

import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.desktopGui.Algorithms
import graphMateKT.graphics.gridGraphics.visualizeGrid
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea

fun handleVizualizeGrid(gridInput: TextArea, algorithmSelector: ComboBox<Algorithms>) {
    val grid: Grid
    val lines = gridInput.text.lines()
    if (lines.all { it == "" })
        return
    val firstLine = lines[0].trim()
    if (firstLine.split(Regex("\\s+")).size == 2) {
        val (width, height) = lines[0].trim().split(Regex("\\s+")).map { it.toIntOrNull() }
        if (width != null && height != null) {
            grid = Grid(width, height)
            grid.visualizeGrid()
            return
        }
    }
    if (lines.any { it.length == lines[0].length }) {
        grid = Grid(lines)
        grid.visualizeGrid()
    } else {
        System.err.println("Invalid grid input. First line should either contain 'width height' or be part of the grid itself. All grid lines should have the same length.")
        return
    }
}