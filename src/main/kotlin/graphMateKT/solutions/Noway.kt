package graphMateKT.solutions

import graphMateKT.*
import graphMateKT.graphClasses.Grid
import graphMateKT.gridGraphics.visualizeGrid
import graphMateKT.readInts
import java.lang.System.currentTimeMillis

internal fun main() {
    val ans = noway()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/noway */
internal fun noway(): String {
    val totalStart = currentTimeMillis()

    // 1. Reading input
    var start = currentTimeMillis()
    val (h, w, n) = readInts(3)
    val g = Grid(w, h)
    val rs = IntArray(n)
    val ms = IntArray(n)
    repeat(n) {
        val (ri, mi) = readInts(2)
        rs[it] = ri
        ms[it] = mi
    }
    printTiming("1. Reading input", start)

    // 2. Deleting nodes
    start = currentTimeMillis()
    g.nodes().forEach { t ->
        val row = t.y
        val col = t.x
        repeat(n) {
            if ((row - col - rs[it]) % ms[it] == 0) {
                g.deleteNodeAtXY(col, row)
                return@forEach
            }
        }
    }
    printTiming("2. Deleting nodes", start)

    // 3. Connecting grid
    start = currentTimeMillis()
    val startTile = Tile(0, 0)
    val endTile = Tile(w - 1, h - 1)
    g.connectGridDefault()
    printTiming("3. Connecting grid", start)

    // 4. First BFS from start
    start = currentTimeMillis()
    g.bfs(startTile)
    val startDistances = g.nodes().map { g.distanceTo(it) }
    val optimalDistance = g.distanceTo(endTile)
    printTiming("4. BFS from start + distances", start)

    // 5. Second BFS from end
    start = currentTimeMillis()
    g.bfs(endTile)
    val endDistances = g.nodes().map { g.distanceTo(it) }
    printTiming("5. BFS from end + distances", start)

    // 6. Building pruned grid
    start = currentTimeMillis()
    val prunedGrid = Grid(w, h, initWithDatalessTiles = false)
    g.nodes().forEachIndexed { i, t ->
        if (startDistances[i] + endDistances[i] == optimalDistance) {
            prunedGrid.addNode(t)
        }
    }
    printTiming("6. Building pruned grid", start)

    // 7. Connecting pruned grid
    start = currentTimeMillis()
    prunedGrid.connectGrid { t ->
        prunedGrid.getStraightNeighbours(t).filter { g.distanceTo(t) > g.distanceTo(it) }
    }
    printTiming("7. Connecting pruned grid", start)

    // 8. Final BFS on pruned grid
    start = currentTimeMillis()
    prunedGrid.bfs(startTile, endTile)
    printTiming("8. BFS on pruned grid", start)

    // 9. Counting paths
    start = currentTimeMillis()
    val result = prunedGrid.nrOfPaths(startTile, endTile, 1_000_000_007).toString()
    printTiming("9. Counting paths", start)

    printTiming("TOTAL TIME", totalStart)
    System.err.println("=".repeat(50))

    return result
}

private fun printTiming(label: String, startTime: Long) {
    val elapsed = (System.currentTimeMillis() - startTime).toDouble()
    System.err.println("%-30s: %8.2f ms".format(label, elapsed))
}