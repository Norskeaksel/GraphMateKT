package graphMateKT.graphics.graphGraphics

import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer
import com.brunomnsilva.smartgraph.graph.GraphEdgeList
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

internal class BidirectionalGraphGraphics : Application() {
    companion object {
        lateinit var graph: BaseGraph<Any>
        var finalPath: List<Any> = emptyList()
        var screenTitle = "Graph visualizer. (Click space to pause and resume animations.)"
        var animationTicTimeOverride: Double? = null
        var startPaused = false
        var closeOnEnd = false
    }


    private var isPaused = startPaused
    override fun start(stage: Stage) {
        val visitationOrder: List<Any>
        val graphVisualizer: GraphEdgeList<Any, Any> = run {
            visitationOrder = graph.currentVisitedNodes()
            graph.convertToVisualizationGraph()
        }
        val animationKeyFrameTime =
            animationTicTimeOverride ?: (10_000.0 / graphVisualizer.numVertices())
        val initialPlacement: SmartPlacementStrategy = InitialNodePlacer()
        val graphView: SmartGraphPanel<Any, Any> = SmartGraphPanel(graphVisualizer, initialPlacement)
        graphView.setAutomaticLayout(true)
        val container = SmartGraphDemoContainer(graphView)
        val graphVisualization = Scene(container)
        stage.apply {
            title = screenTitle
            scene = graphVisualization
            width = LaptopResolution.width
            height = LaptopResolution.height
            isMaximized = true
            show()
        }
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

        timeline.play()

        graphVisualization.setOnKeyPressed { event ->
            if (event.code == KeyCode.SPACE) {
                toggleAnimation(timeline)
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
}

private fun BaseGraph<Any>.convertToVisualizationGraph(): GraphEdgeList<Any, Any> {
    val g = GraphEdgeList<Any, Any>()
    nodes().forEach { node ->
        g.insertVertex(node)
    }
    val zeroWidthSpace = '\u200B'
    var edgeCounter = 0
    val addedEdges = mutableSetOf<Pair<Any, Any>>()
    nodes().forEach { u ->
        val edges = edges(u).ifEmpty { neighbours(u).map { 1.0 to it } }
        edges.forEach { (w, v) ->
            val uv = u to v
            val vu = v to u
            if (uv !in addedEdges && vu !in addedEdges) {
                val uniqueWeightLabel = w.toString() + zeroWidthSpace.toString().repeat(edgeCounter++)
                g.insertEdge(u, v, uniqueWeightLabel)
                addedEdges.add(uv)
                addedEdges.add(vu)
            }
        }
    }
    return g
}
