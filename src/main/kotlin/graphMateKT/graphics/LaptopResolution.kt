package graphMateKT.graphics

import javafx.stage.Screen

internal object LaptopResolution {
    val width: Double get() = Screen.getPrimary().visualBounds.width - 100
    val height: Double get() = Screen.getPrimary().visualBounds.height - 100
}