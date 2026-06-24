package graphMateKT.graphics.desktopGui

import graphMateKT.graphics.LaptopResolution
import graphMateKT.graphics.desktopGui.GUIConstants.guiFontSize
import graphMateKT.graphics.desktopGui.GUIConstants.infoIconFontSize
import graphMateKT.graphics.desktopGui.componentHandlers.handleModeToggling
import graphMateKT.graphics.desktopGui.componentHandlers.handleAlgorithmSelection
import graphMateKT.graphics.desktopGui.componentHandlers.handleVizualizeGraph
import graphMateKT.graphics.desktopGui.componentHandlers.handleVizualizeGrid
import graphMateKT.graphics.desktopGui.componentHandlers.handleVizualizeIntGraph
import graphMateKT.graphics.desktopGui.componentHandlers.toolTip
import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.PrintWriter
import java.io.StringWriter


class DesktopGUI : Application() {
    private val infoIcon = "\uD83D\uDEC8"
    private fun infoIcon(tooltipText: String) = Label(infoIcon).apply {
        font = Font.font(infoIconFontSize)
        tooltip = toolTip(tooltipText)
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
        val algorithmInfoIcon = infoIcon("Select an algorithm to learn about what it does.")

        val startLabel = Label("Start node:")
        val startNode = TextField("0")
        val startNodeInfoIcon = infoIcon("The starting node for search algorithms. (BFS, DFS, Dijkstra)")

        val targetLabel = Label("Target node:")
        val targetNode = TextField("1")
        val targetNodeInfoIcon =
            infoIcon("The target node for search algorithms.\nIf set, vizualize the path to the target node, once it's found.")

        val textFields = GridPane(10.0, 10.0).apply {
            add(startLabel, 0, 0)
            add(startNode, 1, 0)
            add(startNodeInfoIcon, 2, 0)
            add(targetLabel, 0, 1)
            add(targetNode, 1, 1)
            add(targetNodeInfoIcon, 2, 1)
        }
        val columnConstraints = listOf(ColumnConstraints(), ColumnConstraints(100.0))
        textFields.columnConstraints.addAll(columnConstraints)

        val vizualizeGraphBtn = Button("Vizualize Graph")
        val graphInfoIcon = infoIcon("Define nodes and edges of the graph.\nOne entry per line.")
        val vizualizeBtnAndIcon = HBox(10.0, vizualizeGraphBtn, graphInfoIcon)

        val inputLabel = Label("Graph input:").apply { font = Font.font(guiFontSize) }
        val inputInfoIcon = infoIcon(GUIConstants.graphInputInfo)
        val inputLabelAndIcon = HBox(10.0, inputLabel, inputInfoIcon).apply {
            alignment = Pos.CENTER_LEFT
        }

        val graphInput = TextArea()
        graphInput.style = if (gridBtn.isSelected) "-fx-font-family: Monospace" else ""
        graphInput.text = GUIConstants.graphInput
        VBox.setVgrow(graphInput, Priority.ALWAYS)

        val layout =
            VBox(
                10.0,
                visualizerSelector,
                algorithmInfoIcon,
                algorithmSelector,
                textFields,
                vizualizeBtnAndIcon,
                inputLabelAndIcon,
                graphInput,
            )

        val scene = Scene(layout, LaptopResolution.WIDTH, LaptopResolution.HEIGHT)
        scene.root.style = "-fx-font-size: ${guiFontSize}px;"
        stage.title = "GraphMateKT GUI"
        stage.scene = scene
        stage.show()

        val modeBtns = Triple(graphBtn, gridBtn, intGraphBtn)
        visualisationMode.selectedToggleProperty().addListener { _, _, _ ->
            handleModeToggling(graphInput, modeBtns, inputLabel, inputInfoIcon)
        }

        algorithmSelector.setOnAction {
            handleAlgorithmSelection(
                algorithmSelector,
                algorithmInfoIcon,
            )
        }

        vizualizeGraphBtn.setOnAction {
            try {
                if (graphBtn.isSelected)
                    handleVizualizeGraph(graphInput, algorithmSelector, startNode, targetNode)
                else if (gridBtn.isSelected) {
                    handleVizualizeGrid(graphInput, algorithmSelector, startNode, targetNode)
                } else if (intGraphBtn.isSelected) {
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
            headerText = e.message ?: "An unexpected error occurred."
            val stackTrace = StringWriter().also { e.printStackTrace(PrintWriter(it)) }.toString()
            dialogPane.expandableContent = TextArea(stackTrace).apply {
                isEditable = false
                isWrapText = false
            }
        }
        alert.showAndWait()
    }
}
