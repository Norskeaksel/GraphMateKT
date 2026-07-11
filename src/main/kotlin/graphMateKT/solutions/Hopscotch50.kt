package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.graphClasses.Grid
import java.io.InputStream
import kotlin.math.abs


internal fun main() {
    val ans = hopscotch50(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/hopscotch50 */
internal fun hopscotch50(inputStream: InputStream): Int {
    val scanner = InputReader(inputStream)
    val dim = scanner.nextInt()
    val target = scanner.nextInt()
    val grid = Grid(dim, dim)
    val startNodes = mutableListOf<Tile>()
    val endNodes = mutableListOf<Tile>()
    repeat(dim) { y ->
        repeat(dim) { x ->
            val nr = scanner.nextInt()
            val node = Tile(x, y, nr)
            if (nr == 1)
                startNodes.add(node)
            if (nr == target)
                endNodes.add(node)
            grid.addNode(node)
        }
    }
    val nodes = grid.nodes()
    nodes.forEach { u ->
        nodes.forEach { v ->
            if (v.data == u.data as Int + 1) {
                val w = abs(v.y - u.y) + abs(v.x - u.x)
                grid.addEdge(u, v, w)
            }
        }
    }
    var min = Double.MAX_VALUE
    startNodes.forEach { u ->
        grid.dijkstra(u)
        endNodes.forEach { v ->
            val currentMin = grid.distanceTo(v)
            if (currentMin < min) {
                min = currentMin
            }
        }
    }
    return if (min == Double.MAX_VALUE) {
        -1
    } else min.toInt()
}
