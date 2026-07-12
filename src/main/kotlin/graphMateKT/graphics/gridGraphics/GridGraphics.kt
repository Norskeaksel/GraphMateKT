package graphMateKT.graphics.gridGraphics

import graphMateKT.graphClasses.Grid
import graphMateKT.Tile
import graphMateKT.graphics.desktopGui.GUIConstants
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
import javafx.scene.transform.Scale
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
        var screenTitle = "Grid visualizer. (Click space to pause and resume animations.)"
        var animationKeyFrameOverride: Double? = null
        var startPaused = false
        var closeOnEnd = false
        var screenWidthMultiplier: Double = 1.0
    }

    val tilesToAnimate = currentVisitedNodes.ifEmpty { grid.currentVisitedNodes() }
    val ratio = min(grid.width, grid.height).toDouble() / max(grid.width, grid.height)
    val sceneWidth = GUIConstants.height * screenWidthMultiplier
    val sceneHeight = sceneWidth * ratio

    var animationKeyFrameTime =
        Duration.millis(animationKeyFrameOverride ?: (10_000.0 / tilesToAnimate.size.coerceAtLeast(10)))
    val canvas = Canvas(sceneWidth, sceneHeight)
    val gc = canvas.graphicsContext2D
    val xNodes = sceneWidth / (grid.width)
    val yNodes = sceneHeight / grid.height
    val minEdgeLength = xNodes.coerceAtMost(yNodes)

    override fun start(stage: Stage) {
        stage.title = screenTitle
        val root = Group()
        gc.fill = Color.BLACK
        grid.nodes().forEach { node ->
            drawSquare(node.x, node.y, Color.BLACK)
        }
        root.children.add(canvas)
        val scene = Scene(root)
        stage.scene = scene
        stage.width = sceneWidth + 13
        stage.height = sceneHeight + 35 // Account for window title bar
        stage.minWidth = sceneWidth
        stage.minHeight = sceneHeight

        // Scale the canvas (and everything drawn on it) when the window is resized.
        val scale = Scale(1.0, 1.0, 0.0, 0.0)
        canvas.transforms.add(scale)
        scene.widthProperty().addListener { _, _, newWidth ->
            scale.x = newWidth.toDouble() / sceneWidth
        }
        scene.heightProperty().addListener { _, _, newHeight ->
            scale.y = newHeight.toDouble() / sceneHeight
        }
        stage.centerOnScreen()
        stage.show()
        animateVisitedNodes(stage)
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
        (finalPath + listOf(Tile(-1, -1))).forEachIndexed { i, node ->
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
        val normalized = (-1e-6 // -1e-6 to avoid 1.0 and thus trying to interpolate out of bounds
                + (value - min) / (max - min + 1e-6)) // + 1e-6 to avoid dividing by zero.
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