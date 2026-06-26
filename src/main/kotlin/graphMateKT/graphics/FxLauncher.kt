package graphMateKT.graphics

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage

private var fxToolkitNotStarted = true

internal fun launchVisualization(app: Application) {
    if (fxToolkitNotStarted) {
        fxToolkitNotStarted = false
        Application.launch(app::class.java)
        return
    }
    val task = Runnable {
        val stage = Stage()
        stage.initModality(Modality.APPLICATION_MODAL)
        val scene = Scene(VBox())
        stage.scene = scene
        stage.show()
        app.start(stage)
    }
    Platform.runLater(task)
}
