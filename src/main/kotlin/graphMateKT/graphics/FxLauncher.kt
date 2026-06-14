package graphMateKT.graphics

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.layout.VBox
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
        val scene = Scene(VBox(), 1000.0, 1000.0)
        stage.scene = scene
        stage.show()
        app.start(stage)
    }
    Platform.runLater(task)
}
