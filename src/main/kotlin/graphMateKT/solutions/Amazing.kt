package graphMateKT.solutions

import graphMateKT.graphClasses.Grid
import graphMateKT.Tile
import graphMateKT.readString
import kotlin.system.exitProcess

internal fun main() {
    val ans = amazing()
    println(ans)
    System.out.flush()
}

private const val DIM = 201
private val dx = intArrayOf(0, -1, 1, 0)
private val dy = intArrayOf(-1, 0, 0, 1)

private val visited = BooleanArray(DIM * DIM)
/** Solves https://open.kattis.com/problems/amazing */
internal fun amazing(): String {
    val g = Grid(DIM, DIM)
    val s = Tile(DIM / 2, DIM / 2)
    val currentVisited = mutableListOf(s)
    fun dfsMaze(c: Tile, p: Tile) {
        var b = ""
        if (c != p) {
            b = postMove(c, p)
            System.out.flush()
            val response = readString()
            if (response == "wall" || response == "w") {
                return
            }
            currentVisited.add(c)
            if (response == "solved") {
                /*g.visualizeGrid(
                    currentVisitedNodes = currentVisited,
                    nodeDistances = currentVisited.indices.map { it.toDouble() })*/
                exitProcess(0)
            }
            if (response == "wrong")
                error("should not be wrong")
        }
        visited[c.xPlusYTimesWidth(DIM)] = true


        for (i in 0..3) {
            val nextX = c.x + dx[i]
            val nextY = c.y + dy[i]
            val nextTile = Tile(nextX, nextY)
            if (visited[nextTile.xPlusYTimesWidth(DIM)]) continue
            dfsMaze(nextTile, c)
        }

        /*val n = g.getStraightNeighbours(c).filter { !visited[it.xPlusYTimesWidth(DIM)] }
        n.forEach {
            if (visited[it.xPlusYTimesWidth(DIM)]) return@forEach
            dfsMaze(it, c)
        }*/
        if (b == "") return
        println(b)
        System.out.flush()
        currentVisited.add(p)
        val bResponse = readString()
        if (bResponse == "solved" || bResponse == "wrong" || (bResponse != "ok" && bResponse != "k")) {
            exitProcess(1)
        }
    }
    dfsMaze(s, s)
    //g.visualizeGrid(currentVisitedNodes = currentVisited, nodeDistances = currentVisited.indices.map { it.toDouble() })
    return "no way out"
}

private fun postMove(c: Tile, p: Tile) = when {
    c.x == p.x && c.y < p.y -> {
        println("up")
        "down"
    }

    c.x < p.x && c.y == p.y -> {
        println("left")
        "right"
    }

    c.x > p.x && c.y == p.y -> {
        println("right")
        "left"
    }

    c.x == p.x && c.y > p.y -> {
        println("down")
        "up"
    }

    else -> error("invalid move")
}
