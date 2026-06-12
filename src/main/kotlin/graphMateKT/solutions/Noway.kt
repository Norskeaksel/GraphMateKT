package graphMateKT.solutions

import graphMateKT.*
import graphMateKT.graphClasses.Grid
import graphMateKT.readInts

internal fun main() {
    val ans = noway()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/noway */
internal fun noway(): Long {
    val (h, w, n) = readInts(3)
    val g = Grid(w, h)
    repeat(n){
        val (ri, mi) = readInts(2)
        g.nodes().forEach { t ->
            if((t.x - t.y - ri) % mi == 0){
                g.deleteNodeAtXY(t.x, t.y)
            }
        }
    }
    g.connectGrid { t ->
        g.getStraightNeighbours(t).filter { it.x >= t.x && it.y >= t.y }
    }
    val start = Tile(0,0)
    val target = Tile(w-1, h-1)
    // g.visualizeGrid()
    return g.nrOfPaths(start, target, 1_000_000_007L)
}
