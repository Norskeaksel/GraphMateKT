package graphMateKT.graphics.desktopGui.componentHandlers

import graphMateKT.graphics.desktopGui.GUIConstants
import javafx.scene.text.Font
import javafx.scene.text.Text

internal class InfoText : Text() {
    var algorithmInfo = GUIConstants.ALGORITHM_INFO
        set(value) {
            field = value
            refresh()
        }

    var graphInfo = GUIConstants.GRAPH_INFO
        set(value) {
            field = value
            refresh()
        }

    var gridMode = false
        set(value) {
            field = value
            refresh()
        }

    init {
        font = Font.font(GUIConstants.GUI_FONT_SIZE)
        wrappingWidth = 380.0
        refresh()
    }

    private fun refresh() {
        val directedOrWallInfo = if (gridMode) GUIConstants.WALL_NODE_INFO else GUIConstants.DIRECTED_INFO
        text = listOf(
            graphInfo,
            algorithmInfo,
            GUIConstants.START_NODE_INFO,
            GUIConstants.TARGET_NODE_INFO,
            directedOrWallInfo,
        ).joinToString(" ")
    }
}
