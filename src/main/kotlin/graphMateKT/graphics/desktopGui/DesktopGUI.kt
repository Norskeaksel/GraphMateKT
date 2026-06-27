package graphMateKT.graphics.desktopGui

import graphMateKT.graphics.LaptopResolution
import graphMateKT.graphics.desktopGui.GUIConstants.GUI_FONT_SIZE
import graphMateKT.graphics.desktopGui.GUIConstants.INFO_ICON_FONT_SIZE
import graphMateKT.graphics.desktopGui.componentHandlers.*
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.PrintWriter
import java.io.StringWriter


internal class DesktopGUI : Application() {
    private val infoIcon = "\uD83D\uDEC8"
    private fun infoIcon(tooltipText: String) = Label(infoIcon).apply {
        font = Font.font(INFO_ICON_FONT_SIZE)
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

        val directedOrNot = ToggleGroup()
        val directed = RadioButton("Directed")
        directed.isSelected = true
        directed.toggleGroup = directedOrNot
        val undirected = RadioButton("Undirected")
        undirected.toggleGroup = directedOrNot
        val radioBtnsInfoIcon = infoIcon("Weather the graph edges goes one or both ways. Not relevant for grids.")

        val wallLabel = Label("Wall node:")
        val wallNode = TextField("#").apply {
            textFormatter = TextFormatter<String> { change ->
                if (change.controlNewText.length <= 1) change else null
            }
        }
        val wallNodeInfoIcon =
            infoIcon("The character representing walls in the grid. Example: #\nOnly relevant for grids.")


        val inputInfoIcon = infoIcon(GUIConstants.GRAPH_INFO)

        val vizualizeGraphBtn = Button("Vizualize graph")

        // @formatter:off
        val infoRows = GridPane(10.0, 10.0).apply {
            add(algorithmSelector, 0, 0, 5, 1); add(algorithmInfoIcon, 5, 0)
            add(startLabel, 0, 1); add(startNode, 1, 1); add(startNodeInfoIcon, 2, 1)
            add(targetLabel, 3, 1); add(targetNode, 4, 1); add(targetNodeInfoIcon, 5, 1)
            add(directed, 0, 2); add(undirected, 1, 2); add(radioBtnsInfoIcon, 2, 2)
            add(wallLabel, 3, 2); add(wallNode, 4, 2); add(wallNodeInfoIcon, 5, 2)
            add(vizualizeGraphBtn, 0, 3, 2, 1)
            add(inputInfoIcon, 2, 3)
        }
        // @formatter:on
        val columnConstraints = listOf(150.0, 160.0, 50.0, 140.0, 100.0, 50.0).map { ColumnConstraints(it) }
        infoRows.columnConstraints.addAll(columnConstraints)

        val graphInput = TextArea()
        graphInput.style = if (gridBtn.isSelected) "-fx-font-family: Monospace" else ""
        graphInput.text = GUIConstants.GRAPH_INPUT
        VBox.setVgrow(graphInput, Priority.ALWAYS)

        val layout = VBox(10.0, visualizerSelector, infoRows, graphInput)

        val scene = Scene(layout, LaptopResolution.WIDTH, LaptopResolution.HEIGHT)
        scene.root.style = "-fx-font-size: ${GUI_FONT_SIZE}px;"
        stage.title = "GraphMateKT GUI"
        stage.scene = scene
        stage.show()

        val modeBtns = Triple(graphBtn, gridBtn, intGraphBtn)
        visualisationMode.selectedToggleProperty().addListener { _, _, _ ->
            handleModeToggling(graphInput, modeBtns, inputInfoIcon, vizualizeGraphBtn)
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
                    handleVizualizeGraph(graphInput, algorithmSelector, startNode, targetNode, !directed.isSelected)
                else if (gridBtn.isSelected) {
                    handleVizualizeGrid(graphInput, algorithmSelector, startNode, targetNode, wallNode)
                } else if (intGraphBtn.isSelected) {
                    handleVizualizeIntGraph(graphInput, algorithmSelector, startNode, targetNode, !directed.isSelected)
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
