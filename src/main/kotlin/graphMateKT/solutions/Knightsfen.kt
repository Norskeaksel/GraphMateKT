package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.Graph
import java.io.InputStream

private val dx = intArrayOf(-2, -1, 1, 2, -2, -1, 1, 2)
private val dy = intArrayOf(-1, -2, -2, -1, 1, 2, 2, 1)
private val targetBoard = listOf("11111", "01111", "00 11", "00001", "00000")

/** Solves https://open.kattis.com/problems/knightsfen */
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

internal fun getNeighbours(board: List<String>): List<List<String>> {
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
    val graph = Graph()
    graph.addNode(targetBoard)
    graph.connectWithRule {
        getNeighbours(it as List<String>)
    }
    repeat(n) {
        val startingBoard = mutableListOf<String>()
        while (startingBoard.size < 5) {
            val line = scanner.nextLine()!!.trimEnd('\r')
            if (line.isNotEmpty()) startingBoard.add(line.padEnd(5, ' '))
        }
        graph.addNode(startingBoard)
        val distance = graph.bfsBidirectional(startingBoard, targetBoard, 10.0).toInt()
        if (distance <= 10) {
            ans.appendLine("Solvable in $distance move(s).")
        } else {
            ans.appendLine("Unsolvable in less than 11 move(s).")
        }
    }
    return ans.toString()
}
