package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.gridGraphics.visualizeGrid
import java.io.InputStream
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt

internal fun main() {
    dragonballs(System.`in`, 1_000_001)
}

/** Solves https://open.kattis.com/problems/dragonballs */
internal fun dragonballs(inputStream: InputStream, dim: Int) {
    val scanner = InputReader(inputStream)
    var n = scanner.nextInt()
    var potentialPoints = listOf(Tile(dim / 2, dim / 2))
    val currentVisitedNodes = mutableListOf<Tile>()
    val dragonBalls = mutableListOf<Tile>()
    val exploredPoints = mutableListOf<Tile>()
    while (n > 0) {
        val explorePoint = potentialPoints.first()
        printfPoint(explorePoint)
        val d = scanner.nextLong()
        if (d == 0L) {
            n--
            dragonBalls.add(explorePoint)
            continue
        }
        exploredPoints.add(explorePoint.copy(data = d))
        val newPoints = pointsAtDistanceSquared(explorePoint, d, dim)
        potentialPoints = potentialPoints(exploredPoints, newPoints)
        currentVisitedNodes.add(explorePoint)
        currentVisitedNodes.addAll(potentialPoints)
    }
    /*if (dim <= 101) {
        val grid = Grid(dim, dim)
        var c = 0.0
        val centers = exploredPoints.map { it.copy(data = null) }.toSet()
        grid.visualizeGrid(
            currentVisitedNodes = currentVisitedNodes,
            finalPath = dragonBalls,
            nodeDistances = currentVisitedNodes.map {
                if (it in centers) {
                    c++
                }
                c
            },
            screenWidthMultiplier = 1.75,
            startPaused = true
        )
    }*/
}

private fun printfPoint(p: Tile) {
    println("${p.x} ${p.y}")
    System.out.flush()
}

/** (x - x0)^2 + (y - y0)^2 = d
 *
 * -> (y - y0)^2 = d - (x - x0)^2
 *
 * -> y - y0 = sqrt(d - (x - x0)^2)
 *
 * -> y = sqrt(d - (x - x0)^2) + y0
 *
 * c = d - (x - x0)^2
 *
 * -> y = sqrt(c) + y0
 * */
private fun pointsAtDistanceSquared(point: Tile, d: Long, dim: Int): List<Tile> {
    val (x0, y0) = point.run { x.toDouble() to y.toDouble() }
    val r = ceil(sqrt(d.toDouble())).toInt()
    val xRange = (x0 - r).toInt().coerceAtLeast(0)..(x0 + r).toInt().coerceAtMost(dim - 1)
    val points = mutableListOf<Tile>()
    for (x in xRange) {
        val c = d - (x - x0).pow(2)
        if (c < 0) continue
        val y = (sqrt(c) + y0).toInt()
        val my = (-sqrt(c) + y0).toInt()
        val x2 = (x - x0).pow(2).toLong()
        val y2 = (y - y0).pow(2).toLong()
        if (x2 + y2 == d) {
            if (y in 0..<dim)
                points.add(Tile(x, y))
            if (my != y && my in 0..<dim) {
                points.add(Tile(x, my))
            }
        }
    }
    return points
}

private fun potentialPoints(exploredPoints: List<Tile>, newPoints: List<Tile>): List<Tile> {
    val potentialPoints = mutableListOf<Tile>()
    outer@ for (np in newPoints) {
        for (ep in exploredPoints) {
            val d = distance(np, ep)
            if (d < ep.data as Long) {
                continue@outer
            }
        }
        potentialPoints.add(np)
    }
    return potentialPoints
}

private fun distance(p1: Tile, p2: Tile) =
    ((p1.x - p2.x).toDouble().pow(2) + (p1.y - p2.y).toDouble().pow(2)).toLong()
