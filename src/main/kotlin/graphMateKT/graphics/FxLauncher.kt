package graphMateKT.graphics

import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Modality
import javafx.stage.Stage
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors.newSingleThreadScheduledExecutor
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

internal object FxLauncher {

    private var exitWatchdog: ScheduledFuture<*>? // Type is * because specifying it as Unit brings unneeded overhead
            = null
    private const val EXIT_DELAY = 500L // 0.5 seconds
    private val scheduler = newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "graphmate-fx-exit").also { it.isDaemon = true }
    }
    private var fxToolkitNotStarted = true
    private const val MAY_INTERRUPT_IF_RUNNING = false

    private fun emptyRunnable() {}
    private fun stopJavaFx() {
        Platform.exit()
    }

    private fun launchStage(app: Application, closed: CountDownLatch) {
        val stage = Stage()
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.setOnHidden { closed.countDown() }
        app.start(stage)
    }

    private fun launchStage(app: Application) {
        launchStage(app, CountDownLatch(1))
    }

    private fun scheduleExitAfterDelay() {
        exitWatchdog = scheduler.schedule(::stopJavaFx, EXIT_DELAY, TimeUnit.MILLISECONDS)
    }

    fun launch(app: Application) {
        exitWatchdog?.cancel(MAY_INTERRUPT_IF_RUNNING)
        if (fxToolkitNotStarted) {
            fxToolkitNotStarted = false
            Platform.setImplicitExit(false)
            Platform.startup(::emptyRunnable)
        }
        if (Platform.isFxApplicationThread()) {
            launchStage(app)
            return
        }

        val stageClosed = CountDownLatch(1)
        Platform.runLater { launchStage(app, stageClosed) }
        stageClosed.await() // await until the stage is closed
        scheduleExitAfterDelay()
    }
}
