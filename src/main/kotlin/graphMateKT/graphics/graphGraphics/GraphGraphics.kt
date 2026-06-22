package graphMateKT.graphics.graphGraphics

import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy
import graphMateKT.graphClasses.BaseGraph
import graphMateKT.graphics.LaptopResolution
import javafx.animation.KeyFrame
import javafx.animation.PauseTransition
import javafx.animation.Timeline
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import javafx.util.Duration


internal class GraphGraphics : Application() {
    companion object {
        lateinit var graph: BaseGraph<Any>
        var finalPath: List<Any> = emptyList()
        var screenTitle = "JavaFXGraph Visualization"
        var animationTicTimeOverride: Double? = null
        var startPaused = false
        var closeOnEnd = false
    }


    private var isPaused = startPaused
    override fun start(stage: Stage) {
        val visitationOrder: List<Any>
        val graphVisualizer: DigraphEdgeList<Any, Any> = run {
            visitationOrder = graph.currentVisitedNodes()
            graph.convertToVisualizationGraph()
        }
        val animationKeyFrameTime =
            animationTicTimeOverride ?: (10_000.0 / graphVisualizer.numVertices())
        val initialPlacement: SmartPlacementStrategy = InitialNodePlacer()
        val graphView: SmartGraphPanel<Any, Any> = SmartGraphPanel(graphVisualizer, initialPlacement)
        graphView.setAutomaticLayout(true)
        val container = SmartGraphDemoContainer(graphView)
        val scene = Scene(container, LaptopResolution.WIDTH, LaptopResolution.HEIGHT)

        stage.title = screenTitle
        stage.scene = scene
        stage.centerOnScreen()
        stage.show()
        graphView.init()

        val transitionTime = Duration.seconds(3.0)
        val pause = PauseTransition(transitionTime) // Stop graph movement after transitionTime
        pause.setOnFinished {
            graphView.setAutomaticLayout(false)
        }
        pause.play()
        val timeline = Timeline()
        visitationOrder.forEachIndexed { index, vertex ->
            val keyFrame = KeyFrame(Duration.millis(animationKeyFrameTime * index).add(transitionTime), {
                val stylableVertex = graphView.getStylableVertex(vertex)
                stylableVertex?.setStyleClass("visitedVertex")
            })
            timeline.keyFrames.add(keyFrame)
        }
        finalPath.forEachIndexed { index, vertex ->
            val keyFrame = KeyFrame(
                Duration.millis(animationKeyFrameTime * index)
                    .add(Duration.millis(animationKeyFrameTime * visitationOrder.size)).add(transitionTime), {
                    val stylableVertex = graphView.getStylableVertex(vertex)
                    stylableVertex?.setStyleClass("pathVertex")
                })
            timeline.keyFrames.add(keyFrame)
        }

        if (!startPaused) {
            timeline.play()
        }
        scene.setOnKeyPressed { event ->
            if (event.code == KeyCode.SPACE) {
                toggleAnimation(timeline)
            }
        }

        scene.setOnMouseClicked {
            toggleAnimation(timeline)
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
}

private fun BaseGraph<Any>.convertToVisualizationGraph(): DigraphEdgeList<Any, Any> {
    val g = DigraphEdgeList<Any, Any>()
    nodes().forEach { node ->
        g.insertVertex(node)
    }
    nodes().forEach { node ->
        val edges = edges(node).ifEmpty { neighbours(node).map { 1.0 to it } }
        edges.forEach { edge ->
            val fromToWeight = Triple(node, edge.second, edge.first)
            g.insertEdge(node, edge.second, fromToWeight)
        }
    }
    return g
}
