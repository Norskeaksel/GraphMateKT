package graphMateKT.graphics.desktopGui

import graphMateKT.graphics.LaptopResolution
import graphMateKT.graphics.desktopGui.componentHandlers.*
import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.util.Duration
import java.io.PrintWriter
import java.io.StringWriter


class DesktopGUI : Application() {
    private val guiFontSize = 24.0
    private val infoIconFontSize = 40.0
    private var nodesNotSet = true
    private val infoIcon = "\uD83D\uDEC8"
    private val rightPadding = Insets(0.0, 10.0, 0.0, 0.0)

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

        val vizualizeGraphBtn = Button("Vizualize Graph")
        val graphInfoIcon = infoIcon("Define nodes and edges of the graph.\nOne entry per line.")

        val inputLabel = Label("Graph input").apply { font = Font.font(guiFontSize) }
        val inputInfoIcon = infoIcon(ModeText.graphInputInfo)

        val textFields = GridPane(10.0, 10.0).apply {
            add(startLabel, 0, 0)
            add(startNode, 1, 0)
            add(startNodeInfoIcon, 2, 0)
            add(targetLabel, 0, 1)
            add(targetNode, 1, 1)
            add(targetNodeInfoIcon, 2, 1)
            add(inputLabel, 0, 2)
            add(inputInfoIcon, 2, 2)
            add(vizualizeGraphBtn, 0, 3)
            add(graphInfoIcon, 2, 3)
            padding = rightPadding
        }
        val columnConstraints = listOf(ColumnConstraints(), ColumnConstraints(100.0))
        textFields.columnConstraints.addAll(columnConstraints)


        val graphInput = TextArea()
        graphInput.style = if (gridBtn.isSelected) "-fx-font-family: Monospace" else ""
        graphInput.text = ModeText.graphInput
        VBox.setVgrow(graphInput, Priority.ALWAYS)

        val layout = VBox(10.0, visualizerSelector, algorithmSelector, textFields, graphInput)

        val scene = Scene(layout, LaptopResolution.WIDTH, LaptopResolution.HEIGHT)
        scene.root.style = "-fx-font-size: ${guiFontSize}px;"
        stage.title = "GraphMateKT GUI"
        stage.scene = scene
        stage.show()

        visualisationMode.selectedToggleProperty().addListener { _, _, _ ->
            handleModeToggling(graphInput, gridBtn, graphBtn, intGraphBtn, inputLabel, inputInfoIcon)
        }

        algorithmSelector.setOnAction {
            if (nodesNotSet)
                handleAlgorithmSelection(
                    visualisationMode,
                    algorithmSelector,
                    startNode,
                    targetNode,
                    startLabel,
                    targetLabel
                )
        }

        startNode.textProperty().addListener { _, _, _ ->
            if (startNode.isFocused)
                nodesNotSet = false
        }
        vizualizeGraphBtn.setOnAction {
            try {
                if (graphBtn.isSelected)
                    handleVizualizeGraph(graphInput, algorithmSelector, startNode, targetNode)
                else if (gridBtn.isSelected) {
                    handleVizualizeGrid(graphInput, algorithmSelector, startNode, targetNode)
                } else if (intGraphBtn.isSelected) {
                    // verifyIntGraphInput(graphInput)
                    handleVizualizeIntGraph(graphInput, algorithmSelector, startNode, targetNode)
                }
            } catch (e: Exception) {
                showError(stage, e)
            }
        }
    }

    private fun showError(owner: Stage, e: Exception) {
        val alert = Alert(Alert.AlertType.ERROR).apply {
            initOwner(owner)
            initModality(Modality.WINDOW_MODAL)
            title = "Error"
            headerText = e.javaClass.simpleName
            contentText = e.message ?: "An unexpected error occurred."
            dialogPane.style = "-fx-font-size: 13px;"

            val stackTrace = StringWriter().also { e.printStackTrace(PrintWriter(it)) }.toString()
            dialogPane.expandableContent = TextArea(stackTrace).apply {
                isEditable = false
                isWrapText = false
            }
        }
        alert.showAndWait()
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
