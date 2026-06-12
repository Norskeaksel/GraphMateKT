package graphMateKT

import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean

private val fxToolkitStarted = AtomicBoolean(false)

internal fun launchVisualization(createApp: () -> Application) {
    val windowClosed = CountDownLatch(1)
    val task = Runnable {
        val app = createApp()
        val stage = Stage()
        stage.setOnHidden { windowClosed.countDown() }
        app.start(stage)
    }
    if (fxToolkitStarted.compareAndSet(false, true)) {
        Platform.startup {
            Platform.setImplicitExit(false)
            task.run()
        }
    } else {
        Platform.runLater(task)
    }
    windowClosed.await()
}
