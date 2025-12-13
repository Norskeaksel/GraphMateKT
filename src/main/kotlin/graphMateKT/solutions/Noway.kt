package graphMateKT.solutions

import graphMateKT.*
import graphMateKT.graphClasses.Grid
import graphMateKT.gridGraphics.visualizeGrid
import graphMateKT.readInts

internal fun main() {
    val ans = noway()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/noway */
internal fun noway(): String {
    val (h, w, n) = readInts(3)
    val g = Grid(w, h)
    val g2 = Grid(w, h)
    val rs = IntArray(n)
    val ms = IntArray(n)
    repeat(n) {
        val (ri, mi) = readInts(2)
        rs[it] = ri
        ms[it] = mi
    }
    g.nodes().forEach { t ->
        val x = t.x
        val y = t.y
        repeat(n) {
            if ((x - y - rs[it]) % ms[it] == 0) {
                g.deleteNodeAtXY(x, y)
                g2.deleteNodeAtXY(x, y)
                return@forEach
            }
        }
    }
    val start = Tile(0, 0)
    val end = Tile(w - 1, h - 1)
    g.connectGridDefault()
    g2.connectGridDefault()
    g.bfs(start)
    g2.bfs(end)
    val optimalDistance = g.distanceTo(end)
    val prunedGrid = Grid(w, h, initWithDatalessTiles = false)
    g.nodes().forEach { t ->
        val d1 = g.distanceTo(t)
        val d2 = g2.distanceTo(t)
        if (d1 >= 0 && d2 >= 0 && d1 + d2 == optimalDistance) {
            prunedGrid.addNode(t)
        }
    }
    prunedGrid.connectGrid { t ->
        prunedGrid.getStraightNeighbours(t).filter { g.distanceTo(t) < g.distanceTo(it) }
    }
    prunedGrid.bfs(start, end)
    // prunedGrid.visualizeGrid()
    return prunedGrid.nrOfPaths(start, end, 1_000_000_007).toString()
}
