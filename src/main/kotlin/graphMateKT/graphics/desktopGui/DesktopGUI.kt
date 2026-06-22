package graphMateKT.graphics.desktopGui

import graphMateKT.graphics.LaptopResolution
import graphMateKT.graphics.desktopGui.componentHandlers.handleNodeSetting
import graphMateKT.graphics.desktopGui.componentHandlers.handleVizualizeGraph
import graphMateKT.graphics.desktopGui.componentHandlers.handleVizualizeGrid
import graphMateKT.graphics.desktopGui.componentHandlers.handleVizualizeIntGraph
import javafx.application.Application
import javafx.geometry.Insets;
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
    private val infoIcon = "\uD83D\uDEC8"

    private fun infoIcon(tooltipText: String) = Label(infoIcon).apply {
        font = Font.font(infoIconFontSize)
        tooltip = Tooltip(tooltipText)
            .apply {
                font = Font.font(guiFontSize)
                showDelay = Duration.ZERO
            }
    }

    override fun start(stage: Stage) {

        val visualisationMode = ToggleGroup()
        val graphBtn = ToggleButton("Vizualize Graph").apply { isSelected = true }
        val gridBtn = ToggleButton("Vizualize Grid")
        val intGraphBtn = ToggleButton("Vizualize IntGraph")
        graphBtn.toggleGroup = visualisationMode
        gridBtn.toggleGroup = visualisationMode
        intGraphBtn.toggleGroup = visualisationMode
        val visualizerSelector = HBox(10.0, graphBtn, gridBtn, intGraphBtn)

        val algorithmSelector = ComboBox<Algorithms>()
        algorithmSelector.items.addAll(Algorithms.entries)
        algorithmSelector.promptText = "Select algorithm"

        val startLabel = Label("Start node:")
        val startNode = TextField()
        val startNodeInfoIcon = infoIcon("The starting node for search algorithms. (BFS, DFS, Dijkstra)")

        val targetLabel = Label("Target node:")
        val targetNode = TextField()
        val targetNodeInfoIcon =
            infoIcon("The target node for search algorithms.\nIf set, vizualize the path to the target node, once it's found.")

        val textFields = GridPane(10.0, 10.0).apply {
            add(startLabel, 0, 0)
            add(startNode, 1, 0)
            add(startNodeInfoIcon, 2, 0)
            add(targetLabel, 0, 1)
            add(targetNode, 1, 1)
            add(targetNodeInfoIcon, 2, 1)
            padding = Insets(0.0, 10.0, 0.0, 10.0)
        }

        val vizualizeGraphBtn = Button("Vizualize Graph")
        val graphInfoIcon = infoIcon("Define nodes and edges of the graph.\nOne entry per line.")
        val vizualizeBtnAndIcon = HBox(10.0, vizualizeGraphBtn, graphInfoIcon)

        val graphInput = TextArea()
        graphInput.style = if (gridBtn.isSelected) "-fx-font-family: Monospace" else ""
        graphInput.text = ExampleInput.graphInput
        VBox.setVgrow(graphInput, Priority.ALWAYS)

        val layout =
            VBox(10.0, visualizerSelector, algorithmSelector, textFields, vizualizeBtnAndIcon, graphInput)

        val scene = Scene(layout, LaptopResolution.WIDTH, LaptopResolution.HEIGHT)
        scene.root.style = "-fx-font-size: ${guiFontSize}px;"
        stage.title = "GraphMateKT GUI"
        stage.scene = scene
        stage.show()

        visualisationMode.selectedToggleProperty().addListener { _, _, _ ->
            graphInput.style = if (gridBtn.isSelected) "-fx-font-family: Monospace" else ""
            if(graphBtn.isSelected) {
                graphInput.text = ExampleInput.graphInput
            }
            if(gridBtn.isSelected) {
                graphInput.text = ExampleInput.gridInput
            }
            if(intGraphBtn.isSelected) {
                graphInput.text = ExampleInput.intGraphInput
            }
        }

        algorithmSelector.setOnAction {
            if (nodesNotSet)
                handleNodeSetting(visualisationMode, algorithmSelector, startNode, targetNode, startLabel, targetLabel)
        }

        startNode.textProperty().addListener { _, _, _ ->
            if (startNode.isFocused)
                nodesNotSet = false
        }

        vizualizeGraphBtn.setOnAction {
            if (graphBtn.isSelected)
                handleVizualizeGraph(graphInput, algorithmSelector, startNode, targetNode)
            else if (gridBtn.isSelected) {
                handleVizualizeGrid(graphInput, algorithmSelector, startNode, targetNode)
            } else if (intGraphBtn.isSelected) {
                verifyIntGraphInput(graphInput)
                handleVizualizeIntGraph(graphInput, algorithmSelector, startNode, targetNode)
            }
        }
    }

    private fun verifyIntGraphInput(graphInput: TextArea) {
        require(graphInput.text.lines().run {
            val space = Regex("\\s+")
            first().split(space).run { size == 2 && all { it.toIntOrNull() != null } }
                    && all { line ->
                line.split(space).run {
                    size in 2..3 && all { it.toDoubleOrNull() != null }
                }
            }
        }
        )
    }
}