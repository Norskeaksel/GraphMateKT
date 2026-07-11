package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.graphClasses.Grid
// import graphMateKT.graphics.gridGraphics.visualizeGrid
import java.io.InputStream


internal fun main() {
    val ans = terraces(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/Terraces */
internal fun terraces(inputStream: InputStream): Int {
    val scanner = InputReader(inputStream)
    val width = scanner.nextInt()
    val height = scanner.nextInt()
    val grid = Grid(width, height)
    for (y in 0 until height) {
        for (x in 0 until width) {
            val height = scanner.nextInt()
            grid.addNode(Tile(x, y, height))
        }
    }
    grid.connectGrid { t ->
        grid.getStraightNeighbours(t).filter { it.data == t.data }
    }
    val plateaus = mutableListOf<List<Tile>>()
    grid.nodes().forEach {
        grid.bfs(it, reset = false)
        plateaus.add(grid.currentVisitedNodes())
    }
    val bottoms = mutableListOf<Tile>()
    plateaus.forEach { platau ->
        if (platau.none { u -> grid.getStraightNeighbours(u).any { v -> (v.data as Int) < u.data as Int } }) {
            bottoms.addAll(platau)
        }
    }
    /*val nodeDistances = plateaus.mapIndexed { i, it -> it.map { i.toDouble() } }.flatten()
    grid.visualizeGrid(
        currentVisitedNodes = plateaus.flatten(),
        nodeDistances = nodeDistances,
        finalPath = bottoms,
        animationTicTimeOverride = 200.0,
        startPaused = true
    )*/
    return bottoms.size
}
