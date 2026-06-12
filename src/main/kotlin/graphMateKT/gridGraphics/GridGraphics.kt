package graphMateKT.gridGraphics

import graphMateKT.graphClasses.Grid
import graphMateKT.Tile
import javafx.animation.KeyFrame
import javafx.animation.PauseTransition
import javafx.animation.Timeline
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Duration
import kotlin.math.max
import kotlin.math.min


internal class GridGraphics : Application() {
    companion object {
        lateinit var grid: Grid
        var currentVisitedNodes: List<Tile> = emptyList()
        var finalPath: List<Tile> = emptyList()
        var nodeDistances: List<Double> = emptyList()
        var screenTitle = "Grid visualizer (Click or space to pause and resume)"
        var animationKeyFrameOverride: Double? = null
        var startPaused = false
        var closeOnEnd = false
        var screenWidthOverride: Double? = null
    }

    val tilesToAnimate = if (currentVisitedNodes.isEmpty()) grid.currentVisitedNodes() else currentVisitedNodes
    val ratio = min(grid.width, grid.height).toDouble() / max(grid.width, grid.height)
    val sceneWith = screenWidthOverride ?: 1000.0
    val sceneHeight = sceneWith * ratio

    var animationKeyFrameTime =
        Duration.millis(animationKeyFrameOverride ?: (10_000.0 / tilesToAnimate.size.coerceAtLeast(10)))
    val canvas = Canvas(sceneWith, sceneHeight)
    val gc = canvas.graphicsContext2D
    val xNodes = sceneWith / (grid.width)
    val yNodes = sceneHeight / grid.height
    val minEdgeLength = xNodes.coerceAtMost(yNodes)

    override fun start(primaryStage: Stage) {
        primaryStage.title = screenTitle
        val root = Group()
        gc.fill = Color.BLACK
        grid.nodes().forEach { node ->
            drawSquare(node.x, node.y, Color.BLACK)
        }
        root.children.add(canvas)
        primaryStage.scene = Scene(root)
        primaryStage.show()
        animateVisitedNodes(primaryStage)
    }

    private fun drawSquare(x: Int, y: Int, color: Color) {
        gc.fill = color
        gc.fillRect(x * minEdgeLength, y * minEdgeLength, minEdgeLength - 1, minEdgeLength - 1)
    }

    private var isPaused = startPaused
    private fun animateVisitedNodes(stage: Stage) {
        val scene = stage.scene
        val timeline = Timeline()
        println("animationKeyFrameTime: $animationKeyFrameTime")
        tilesToAnimate.forEachIndexed { i, node ->
            val color = getInterpolatedColor((nodeDistances[i]), nodeDistances.maxOrNull() ?: 1.0)
            val keyFrame = KeyFrame(
                animationKeyFrameTime.multiply(i.toDouble()), squareDrawer(
                    node, color
                )
            )
            timeline.keyFrames.add(keyFrame)
        }
        // Add extra tile to avoid closing on end too soon
        (finalPath + listOf(Tile(-1,-1))).forEachIndexed { i, node ->
            val keyFrame = KeyFrame(
                animationKeyFrameTime.multiply(1.05 * (i.toDouble() + tilesToAnimate.size + 1)),
                squareDrawer(
                    node, Color.GREEN
                )
            )
            timeline.keyFrames.add(keyFrame)
        }

        val pause = PauseTransition(Duration.seconds(.5))
        pause.setOnFinished { timeline.play() }
        if (!startPaused)
            pause.play()

        scene.setOnKeyPressed { event ->
            if (event.code == KeyCode.SPACE) {
                toggleAnimation(timeline)
            }
        }

        scene.setOnMouseClicked {
            toggleAnimation(timeline)
        }

        timeline.setOnFinished {
            if (closeOnEnd) {
                stage.close()
            }
        }
    }

    private fun toggleAnimation(timeline: Timeline) {
        if (isPaused) {
            timeline.play()
            isPaused = false
        } else {
            timeline.pause()
            isPaused = true
        }
    }

    private fun squareDrawer(node: Tile, color: Color): (ActionEvent) -> Unit {
        return { drawSquare(node.x, node.y, color) }
    }


    private fun getInterpolatedColor(value: Double, max: Double, min: Double = 0.0): Color {
        if (value !in min..max) {
            error("Value $value is not in range $min..$max")
        }
        val normalized =
            (value - min) / (max - min) - 1e-6 // -1e-6 to avoid 1.0 and thus trying to interpolate out of bounds
        val colors = arrayOf(
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.LIGHTSKYBLUE,
            Color.BLUE,
        )
        val index = (normalized * (colors.size - 1)).toInt()
        return colors[index].interpolate(colors[index + 1], (normalized * (colors.size - 1)) % 1)
    }
}
