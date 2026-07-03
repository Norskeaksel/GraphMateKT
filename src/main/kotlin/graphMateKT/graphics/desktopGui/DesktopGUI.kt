package graphMateKT.graphics.desktopGui

import graphMateKT.graphics.desktopGui.GUIConstants.GUI_FONT_SIZE
import graphMateKT.graphics.desktopGui.componentHandlers.*
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.PrintWriter
import java.io.StringWriter


internal class DesktopGUI : Application() {

    override fun start(stage: Stage) {

        val visualisationMode = ToggleGroup()
        val graphBtn = ToggleButton("Visualize Graph").apply { isSelected = true; toggleGroup = visualisationMode }
        val gridBtn = ToggleButton("Visualize Grid").apply { toggleGroup = visualisationMode }
        val intGraphBtn = ToggleButton("Visualize IntGraph").apply { toggleGroup = visualisationMode }
        val visualizerSelector = HBox(10.0, graphBtn, gridBtn, intGraphBtn)

        val algorithmSelector =
            ComboBox<Algorithms>().apply { items.addAll(Algorithms.entries); promptText = "Select algorithm" }

        val startLabel = Label("Start node:")
        val startNode = TextField("0")

        val targetLabel = Label("Target node:")
        val targetNode = TextField("1")

        val directedOrNot = ToggleGroup()
        val directed = RadioButton("Directed").apply { isSelected = true; toggleGroup = directedOrNot }
        val undirected = RadioButton("Undirected").apply { toggleGroup = directedOrNot }

        val wallLabel = Label("Wall node:")
        val wallNode = TextField("#").apply {
            textFormatter = TextFormatter<String> { change ->
                if (change.controlNewText.length <= 1) change else null
            }
        }

        val infoText = InfoText()
        val vizualizeBtn = Button("Visualize Graph")

        // @formatter:off
        val infoRows = GridPane(10.0, 10.0).apply {
            add(algorithmSelector, 0, 0, 2, 1)
            add(startLabel, 0, 1); add(startNode, 1, 1)
            add(targetLabel, 0, 2); add(targetNode, 1, 2)
            add(directed, 0, 3); add(undirected, 1, 3)
            add(wallLabel, 0, 3); add(wallNode, 1, 3)
            add(vizualizeBtn, 0, 4, 2, 1)
            add(infoText, 2, 0, 1, 5)
        }
        // @formatter:on
        val col0 = 190.0
        val col1 = 200.0
        infoRows.columnConstraints.addAll(ColumnConstraints(col0), ColumnConstraints(col1))
        val radioBtnRow = listOf(directed, undirected)
        val wallRow = listOf(wallLabel, wallNode).onEach {
            it.isVisible = false; it.isManaged = false
        }

        infoText.wrappingWidthProperty()
            .bind(infoRows.widthProperty().subtract(col0 + col1 + 3 * infoRows.hgap))

        val graphInput = TextArea()
        graphInput.style = if (gridBtn.isSelected) "-fx-font-family: Monospace" else ""
        graphInput.text = GUIConstants.GRAPH_INPUT
        VBox.setVgrow(graphInput, Priority.ALWAYS)

        val layout = VBox(10.0, visualizerSelector, infoRows, graphInput)
        val gui = Scene(layout, GUIConstants.width, GUIConstants.height).apply {
            root.style = "-fx-font-size: ${GUI_FONT_SIZE}px;"
        }
        stage.run {
            title = "GraphMateKT GUI"
            scene = gui
            show()
        }

        val modeBtns = Triple(graphBtn, gridBtn, intGraphBtn)
        visualisationMode.selectedToggleProperty().addListener { _, _, _ ->
            handleModeToggling(graphInput, modeBtns, radioBtnRow, wallRow, infoText, vizualizeBtn)
        }

        algorithmSelector.setOnAction {
            handleAlgorithmSelection(
                algorithmSelector,
                infoText,
            )
        }

        vizualizeBtn.setOnAction {
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
