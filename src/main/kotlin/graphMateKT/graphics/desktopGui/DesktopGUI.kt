package graphMateKT.graphics.desktopGui

import graphMateKT.graphClasses.Graph
import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.LaptopResolution
import graphMateKT.graphics.graphGraphics.visualizeGraph
import graphMateKT.graphics.gridGraphics.visualizeGrid
import javafx.application.Application
import javafx.scene.Scene
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.util.Duration

const val GUI_FONT_SIZE = 24.0
const val INFO_ICON_FONT_SIZE = 40.0

class DesktopGUI : Application() {
    override fun start(stage: Stage) {
        val algorithmSelector = ComboBox<Algorithms>()
        algorithmSelector.items.addAll(Algorithms.entries)
        algorithmSelector.promptText = "Select algorithm"

        val vizualizeGraphBtn = Button("Vizualize Graph")
        val vizualizeGridBtn = Button("Vizualize Grid")

        val infoIconUnicode = "\uD83D\uDEC8"
        val graphInfoIcon = Label(infoIconUnicode).apply {
            font = Font.font(INFO_ICON_FONT_SIZE)
            tooltip = Tooltip("Define nodes and edges of the graph.\nOne entry per line.")
                .apply {
                    font = Font.font(GUI_FONT_SIZE)
                    showDelay = Duration.ZERO
                }
        }
        val gridInfoIcon = Label(infoIconUnicode).apply {
            font = Font.font(INFO_ICON_FONT_SIZE)
            tooltip =
                Tooltip("Define the width and height of the grid (example: 50 50) OR paste a rectangle of characters")
                    .apply {
                        font = Font.font(GUI_FONT_SIZE)
                        showDelay = Duration.ZERO
                    }
        }

        val graphSpacer = Region()
        HBox.setHgrow(graphSpacer, Priority.ALWAYS)
        val gridSpacer = Region()
        HBox.setHgrow(gridSpacer, Priority.ALWAYS)

        val graphBtns = HBox(10.0, vizualizeGraphBtn, graphSpacer, graphInfoIcon)
        val gridBtns = HBox(10.0, vizualizeGridBtn, gridSpacer, gridInfoIcon)
        HBox.setMargin(graphInfoIcon, Insets(0.0, 10.0, 0.0, 0.0))
        HBox.setMargin(gridInfoIcon, Insets(0.0, 10.0, 0.0, 0.0))

        val graphInput = TextArea()
        val gridInput = TextArea()
        graphInput.text = """0 1 10.0
0 2 3.0
1 3
2 1 4.0
2 3 8.0
2 4 2.0
5
3 4 5.0"""
        gridInput.style = "-fx-font-family: Monospace"
        gridInput.text = """
           S....#E
           ###.##.
           ..#.#..
           #.#.#.#
           .......
        """.trimIndent()

        val graphVBox = VBox(5.0, graphBtns, graphInput)
        val gridVBox = VBox(5.0, gridBtns, gridInput)
        // Make the components symmetrical
        graphVBox.prefWidth = 0.0
        gridVBox.prefWidth = 0.0

        VBox.setVgrow(graphInput, Priority.ALWAYS)
        VBox.setVgrow(gridInput, Priority.ALWAYS)

        val graphAndGridComponents = HBox(10.0, graphVBox, gridVBox)
        HBox.setHgrow(graphVBox, Priority.ALWAYS)
        HBox.setHgrow(gridVBox, Priority.ALWAYS)


        val layout = VBox(10.0, algorithmSelector, graphAndGridComponents)
        VBox.setVgrow(graphAndGridComponents, Priority.ALWAYS)

        val scene = Scene(layout, LaptopResolution.WIDTH, LaptopResolution.HEIGHT)
        scene.root.style = "-fx-font-size: ${GUI_FONT_SIZE}px;"
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
                val (width, height) = lines[0].trim().split(Regex("\\s+")).map { it.toIntOrNull() }
                if (width != null && height != null) {
                    grid = Grid(width, height)
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