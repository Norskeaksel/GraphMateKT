package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.gridGraphics.visualizeGrid
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
        grid.getStraightNeighbours(t).filter { (it.data as Int) <= (t.data as Int) }
    }
    val potentialBottoms =
        grid.nodes().filter { t -> grid.getStraightNeighbours(t).all { it.data as Int >= t.data as Int } }.sortedBy { it.data as Int }
    val trueBottoms = mutableSetOf<Tile>()
    val currentVisited = mutableListOf<Tile>()
    potentialBottoms.forEach { pb ->
        grid.bfs(pb)
        val currentVisitedNodes = grid.currentVisitedNodes()
        if (currentVisitedNodes.isEmpty()) return@forEach
        trueBottoms.addAll(currentVisitedNodes.filter {it.data == pb.data})
    }
    //grid.visualizeGrid(currentVisitedNodes = currentVisited, finalPath = trueBottoms.toList(), startPaused = true)
    return trueBottoms.size
}
