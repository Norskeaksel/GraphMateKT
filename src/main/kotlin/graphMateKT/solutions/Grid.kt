package graphMateKT.solutions

import graphMateKT.graphClasses.Grid
import graphMateKT.Tile
import graphMateKT.readInts
import graphMateKT.readString

internal fun main() {
    val ans = grid()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/grid */
internal fun grid(): Int {
    val (n, m) = readInts(2)
    val lines = mutableListOf<String>()
    repeat(n) {
        val line = readString()
        lines.add(line)
    }
    val grid = Grid(lines)
    grid.connectWithRule { t ->
        val nr = t.data as Char - '0'
        val neighbours = listOfNotNull(
            grid.xy2NodeOrNull(t.x + nr, t.y),
            grid.xy2NodeOrNull(t.x - nr, t.y),
            grid.xy2NodeOrNull(t.x, t.y + nr),
            grid.xy2NodeOrNull(t.x, t.y - nr)
        )
        neighbours
    }
    val start = Tile(0, 0)
    val end = Tile(m - 1, n - 1)
    grid.bfs(start, end)
    val distance = grid.distanceTo(end).toInt()
    return if (distance == Int.MAX_VALUE) -1 else distance
}
