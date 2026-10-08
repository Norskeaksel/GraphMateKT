package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.Graph
import java.io.InputStream

private val dx = intArrayOf(-2, -1, 1, 2, -2, -1, 1, 2)
private val dy = intArrayOf(-1, -2, -2, -1, 1, 2, 2, 1)
private val targetBoard = listOf("11111", "01111", "00 11", "00001", "00000")
internal fun main() {
    val ans = knightsfen(System.`in`)
    println(ans)
    System.out.flush()
}

private fun boardsEmptyCoordinates(board: List<String>): Pair<Int, Int> {
    val y = board.indexOfFirst { row -> row.contains(' ') }
    val x = board[y].indexOf(' ')
    return x to y
}

fun getNeighbours(board: List<String>): List<List<String>> {
    val (x, y) = boardsEmptyCoordinates(board)
    val neighbours = mutableListOf<List<String>>()
    dx.indices.forEach { i ->
        val nx = x + dx[i]
        val ny = y + dy[i]
        if (nx !in 0..4 || ny !in 0..4) return@forEach
        val mutableBoard = Array(5) { board[it].toCharArray() }
        mutableBoard[y][x] = board[ny][nx]
        mutableBoard[ny][nx] = ' '
        neighbours.add(mutableBoard.map { it.joinToString("") })
    }
    return neighbours
}

internal fun knightsfen(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val n = scanner.nextInt()
    val ans = StringBuilder()
    val graph = Graph(isSparse = true)
    graph.addNode(targetBoard)
    graph.connectWithRule {
        getNeighbours(it as List<String>)
    }
    graph.bfs(listOf(targetBoard), maxDepth = 10)
    repeat(n) {
        val startingBoard = mutableListOf<String>()
        repeat(5) { y ->
            val line = scanner.nextLine()!!.padEnd(5, ' ')
            startingBoard.add(line)
        }
        val distance = graph.distanceTo(startingBoard).toInt()
        if (distance <= 10) {
            ans.appendLine("Solvable in $distance move(s).")
        } else {
            ans.appendLine("Unsolvable in less than 11 move(s).")
        }
    }
    return ans.toString()
}
