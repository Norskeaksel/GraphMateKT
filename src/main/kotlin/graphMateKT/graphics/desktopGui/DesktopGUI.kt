package graphMateKT.graphics.desktopGui

import graphMateKT.graphics.LaptopResolution
import graphMateKT.graphics.desktopGui.componentHandlers.handleNodeSetting
import graphMateKT.graphics.desktopGui.componentHandlers.handleVizualizeGraph
import graphMateKT.graphics.desktopGui.componentHandlers.handleVizualizeGrid
import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.util.Duration

class DesktopGUI : Application() {
    private val guiFontSize = 24.0
    private val infoIconFontSize = 40.0
    private var nodesNotSet = true

    override fun start(stage: Stage) {
        val algorithmSelector = ComboBox<Algorithms>()
        algorithmSelector.items.addAll(Algorithms.entries)
        algorithmSelector.promptText = "Select algorithm"

        val startNodeLabel = Label("Start node:")
        val startNode = TextField()
        val startNodesBox = HBox(10.0, startNodeLabel, startNode)

        val targetNodeLabel = Label("Target node:")
        val targetNode = TextField()
        val targetNodesBox = HBox(10.0, targetNodeLabel, targetNode)

        val vizualizeGraphBtn = Button("Vizualize Graph")
        val vizualizeGridBtn = Button("Vizualize Grid")

        val infoIconUnicode = "\uD83D\uDEC8"
        val graphInfoIcon = Label(infoIconUnicode).apply {
            font = Font.font(infoIconFontSize)
            tooltip = Tooltip("Define nodes and edges of the graph.\nOne entry per line.")
                .apply {
                    font = Font.font(guiFontSize)
                    showDelay = Duration.ZERO
                }
        }
        val gridInfoIcon = Label(infoIconUnicode).apply {
            font = Font.font(infoIconFontSize)
            tooltip =
                Tooltip("Define the width and height of the grid (example: 50 50) OR paste a rectangle of characters")
                    .apply {
                        font = Font.font(guiFontSize)
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

        val gridPane = GridPane(10.0, 10.0)
        gridPane.add(algorithmSelector, 0, 0)
        gridPane.add(startNodesBox, 0, 1)
        gridPane.add(targetNodesBox, 1, 1)
        gridPane.add(graphBtns, 0, 2)
        gridPane.add(gridBtns, 1, 2)
        gridPane.add(graphInput, 0, 3)
        gridPane.add(gridInput, 1, 3)
        repeat(2) {
            val columnConstraints = ColumnConstraints()
            columnConstraints.percentWidth = 50.0
            gridPane.columnConstraints.add(columnConstraints)
        }
        GridPane.setVgrow(graphInput, Priority.ALWAYS)
        GridPane.setVgrow(gridInput, Priority.ALWAYS)

        val scene = Scene(gridPane, LaptopResolution.WIDTH, LaptopResolution.HEIGHT)
        scene.root.style = "-fx-font-size: ${guiFontSize}px;"
        stage.title = "GraphMateKT"
        stage.scene = scene
        stage.show()

        algorithmSelector.setOnAction {
            if(nodesNotSet)
                handleNodeSetting(algorithmSelector, startNodeLabel, startNode, targetNode)
        }

        startNode.textProperty().addListener { _, _, _ ->
            if (startNode.isFocused)
                nodesNotSet = false
        }

        vizualizeGraphBtn.setOnAction {
            handleVizualizeGraph(graphInput, algorithmSelector, startNode, targetNode)
        }

        vizualizeGridBtn.setOnAction {
            handleVizualizeGrid(gridInput, algorithmSelector, startNode, targetNode)
        }
    }
}