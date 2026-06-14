package graphMateKT.graphics.desktopGui

import graphMateKT.graphClasses.Graph
import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.graphGraphics.visualizeGraph
import graphMateKT.graphics.gridGraphics.visualizeGrid
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Stage

class DesktopGUI : Application() {
    companion object {}

    override fun start(stage: Stage) {
        val graphInput = TextArea()
        val gridInput = TextArea()

        val vizualizeGraphBtn = Button("Vizualize Graph")
        val vizualizeGridBtn = Button("Vizualize Grid")

        val graphVBox = VBox(5.0, vizualizeGraphBtn, graphInput)
        val gridVBox = VBox(5.0, vizualizeGridBtn, gridInput)

        VBox.setVgrow(graphInput, Priority.ALWAYS)
        VBox.setVgrow(gridInput, Priority.ALWAYS)

        val hBox = HBox(10.0, graphVBox, gridVBox)
        HBox.setHgrow(graphVBox, Priority.ALWAYS)
        HBox.setHgrow(gridVBox, Priority.ALWAYS)

        val layout = VBox(10.0, hBox)
        VBox.setVgrow(hBox, Priority.ALWAYS)

        val scene = Scene(layout, 1000.0, 1000.0)
        stage.title = "GraphMateKT"
        stage.scene = scene
        stage.show()

        vizualizeGraphBtn.setOnAction {
            val graph = Graph()
            println("Reading graph input")
            val lines = graphInput.text.lines()
            lines.forEachIndexed { i, line ->
                val uvw = line.trim().split(Regex("\\s+"))
                when (uvw.size) {
                    1 -> graph.addNode(uvw[0]).also { println("Adding node: ${uvw[0]}") }
                    2 -> graph.addEdge(uvw[0], uvw[1]).also { println("Adding edge from ${uvw[0]} to ${uvw[1]}") }
                    // @formatter:off
                    3 -> graph.addEdge( uvw[0], uvw[1], uvw[2].toDoubleOrNull() ?: run { System.err.println("Invalid weight '${uvw[2]}'. Defaulting to 1.0"); 1.0 })
                        .also { println("Adding edge from ${uvw[0]} to ${uvw[1]} with weight ${uvw[2].toDoubleOrNull() ?: 1.0}") }
                    // @formatter:on
                    else -> System.err.println("Ignoring invalid input on line ${i + 1}")
                }
            }
            println("Graph building complete")
            graph.visualizeGraph()
        }

        vizualizeGridBtn.setOnAction {
            val grid: Grid
            val lines = gridInput.text.lines()
            if (lines.all { it == "" })
                return@setOnAction
            val firstLine = lines[0].trim()
            if (firstLine.split(Regex("\\s+")).size == 2) {
                val (width, hight) = lines[0].trim().split(Regex("\\s+")).map { it.toIntOrNull() }
                if (width != null && hight != null) {
                    grid = Grid(width, hight)
                    grid.visualizeGrid()
                    return@setOnAction
                }
            }
            if (lines.any { it.length == lines[0].length }) {
                grid = Grid(lines)
                grid.visualizeGrid()
            } else {
                System.err.println("Invalid grid input. First line should either contain 'width height' or be part of the grid itself. All grid lines should have the same length.")
                return@setOnAction
            }
        }
    }
}