package graphMateKT.desktop.gui

import graphMateKT.graphClasses.Graph
import graphMateKT.graphGraphics.visualizeGraph
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.Stage

class DesktopGUI : Application() {
    companion object {}

    override fun start(stage: Stage) {
        val inputField = TextField()
        val vizualizeButton = Button("Vizualize")

        val layout = VBox(10.0, inputField, vizualizeButton)
        val scene = Scene(layout, 300.0, 400.0)
        stage.title = "My GUI"
        stage.scene = scene
        stage.show()

        vizualizeButton.setOnAction { event ->
            val graph = Graph()
            val input = inputField.text.split(" ").map { it.toInt() }
            for(i in input.indices step 2){
                graph.addEdge(input[i], input[i+1])
            }
            graph.visualizeGraph()
        }
    }
}
