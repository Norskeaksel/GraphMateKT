package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.graphClasses.Graph
import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.gridGraphics.visualizeGrid
import java.io.InputStream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

internal fun main() {
    val ans = gridmst(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/gridmst */
internal fun gridmst(inputStream: InputStream, dim: Int = 1000): String {
    val scanner = InputReader(inputStream)
    val n = scanner.nextInt()
    val points = mutableListOf<Tile>()
    repeat(n) {
        val x = scanner.nextInt()
        val y = scanner.nextInt()
        points.add(Tile(x, y))
    }
    val graph = makeGraph(points, dim)
    val (weight, mst) = graph.minimumSpanningTree()
    // mst.visualizeGraph(isBidirectional = true)
    return weight.toInt().toString()
}

private fun makeGraph(points: List<Tile>, dim: Int): Graph {
    // Find tile distances to nearest point
    val grid = Grid(dim, dim)
    grid.connectGridDefault()
    grid.bfs(points)
    val size = dim * dim
    val dist = IntArray(size)
    val roots = IntArray(size) { -1 }
    // Find roots of each point
    points.forEach { roots[it.idGivenWidth(dim)] = it.idGivenWidth(dim) }
    grid.currentVisitedNodes().forEach { node ->
        val nodeId = node.idGivenWidth(dim)
        val nodeDist = grid.distanceTo(node).toInt()
        dist[nodeId] = nodeDist
        val predecessor = grid.getStraightNeighbours(node).firstOrNull {
            val neighbourId = it.idGivenWidth(dim)
            dist[neighbourId] == nodeDist - 1 && roots[neighbourId] != -1
        } ?: node
        roots[nodeId] = roots[predecessor.idGivenWidth(dim)]
    }
    val edges = mutableSetOf<Pair<Int, Int>>()
    val deltas = intArrayOf(-1, 1, -dim, dim) // left, right, up, down
    val borderTiles = mutableSetOf<Tile>()
    // find tiles how has a neighbor that comes from a different root and add an edge between the root points
    roots.forEachIndexed { i, u ->
        val col = i % dim
        deltas.forEach { d ->
            val neighbour = i + d
            if (neighbour !in 0 until size || // out of bounds
                (d == -1 && col == 0) ||            // left-edge
                (d == 1 && col == dim - 1)          // right-edge
            ) return@forEach
            val v = roots[neighbour]
            if (v != u) {
                edges.add(min(u, v) to max(u, v))
                //borderTiles.add(Tile(i % dim, i / dim))
            }
        }
    }
    //grid.visualizeGrid(finalPath = borderTiles.toList() + points, screenTitle = "gridMST border finding", startPaused = true)
    val graph = Graph()
    //Create a graph from the edges between root points
    edges.forEach { edge ->
        val (u, v) = edge
        val w = manhattanDistanceOfIds(u, v, dim)
        graph.connect(u, v, w)
    }
    // graph.visualizeGraph(isBidirectional = true)
    return graph
}

private fun manhattanDistanceOfIds(id1: Int, id2: Int, dim: Int): Int =
    abs(id1 % dim - id2 % dim) + abs(id1 / dim - id2 / dim)
