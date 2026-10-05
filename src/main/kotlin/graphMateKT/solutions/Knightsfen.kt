package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.debug
import graphMateKT.graphClasses.IntGraph
import java.io.InputStream
import kotlin.system.measureTimeMillis

private const val NR_OF_COMBINATIONS = 67_603_900 // 25! / (12! * 12!)
internal const val KNIGHT_ORDERS = 2_704_156 // 24! / (12! * 12!)
private val dx = intArrayOf(-2, -1, 1, 2, -2, -1, 1, 2)
private val dy = intArrayOf(-1, -2, -2, -1, 1, 2, 2, 1)
private val pascalsTriangle = Array(25) { IntArray(25) }.also { pt ->
    for (y in 0..24) {
        pt[y][0] = 1
        pt[y][y] = 1
        for (x in 1 until y) {
            pt[y][x] = pt[y - 1][x - 1] + pt[y - 1][x]
        }
    }
}

internal fun main() {
    val ans = knightsfen(System.`in`)
    println(ans)
    System.out.flush()
}

private fun nrOfCombinations(nrOfBits: Int, nrOfZeros: Int): Int =
    if (nrOfZeros < 0) 0 else pascalsTriangle[nrOfBits][nrOfZeros]

internal tailrec fun bitcode2Rank(bitCode: String, nrOfZeros: Int, bitIndex: Int = 0, rankSoFar: Int = 0): Int = when {
    nrOfZeros == 0 || bitIndex >= bitCode.length -> rankSoFar
    bitCode[bitIndex] == '0' -> bitcode2Rank(bitCode, nrOfZeros - 1, bitIndex + 1, rankSoFar)
    else -> {
        val remainingBits = bitCode.lastIndex - bitIndex
        val newRankSoFar = rankSoFar + nrOfCombinations(remainingBits, nrOfZeros - 1)
        bitcode2Rank(bitCode, nrOfZeros, bitIndex + 1, newRankSoFar)
    }
}

private fun boardsEmptyCoordinates(board: List<String>): Pair<Int, Int> =
    board.indexOfFirst { row -> row.contains(' ') }.let { y ->
        board[y].indexOf(' ').let { x ->
            x to y
        }
    }


internal fun board2Id(board: List<String>): Int {
    val (x, y) = boardsEmptyCoordinates(board)
    val bitCode = board.joinToString("").replace(" ", "").replace("\r", "")
    return bitcode2Rank(bitCode, 12) + KNIGHT_ORDERS * Tile(x, y).idGivenWidth(5)
}

internal fun rank2Bitcode(rank: Int, nrOfZeros: Int): String {
    val nrOfBits = nrOfZeros * 2
    var remainingZeros = nrOfZeros
    var remainingRank = rank
    return CharArray(nrOfBits) { bitIndex ->
        val remainingBits = nrOfBits - bitIndex - 1
        val remainingZeroCombinations = nrOfCombinations(remainingBits, remainingZeros - 1)
        if (remainingRank < remainingZeroCombinations) '0'.also { remainingZeros-- }
        else '1'.also { remainingRank -= remainingZeroCombinations }
    }.joinToString("")
}

internal fun id2Board(id: Int): List<String> {
    val emptyTileId = id / KNIGHT_ORDERS
    val bitCode = rank2Bitcode(id % KNIGHT_ORDERS, 12)
    return List(5) { y ->
        CharArray(5) { x ->
            val tileId = x + y * 5
            when {
                tileId == emptyTileId -> ' '
                tileId < emptyTileId -> bitCode[tileId]
                else -> bitCode[tileId - 1]
            }
        }.joinToString("")
    }
}

/** Solves https://open.kattis.com/problems/knightsfen */
internal fun knightsfen(inputStream: InputStream): String {
    val graph = IntGraph(NR_OF_COMBINATIONS)
    graph.connectWithRule { u ->
        val board = id2Board(u)
        val neighbours = mutableListOf<Int>()
        dx.indices.forEach { i ->
            val mutableBoard = board.map { it.toCharArray() }.toMutableList()
            val (x, y) = boardsEmptyCoordinates(board)
            val nx = x + dx[i]
            val ny = y + dy[i]
            if (nx !in 0..4 || ny !in 0..4) return@forEach
            mutableBoard[y][x] = board[nx][ny]
            mutableBoard[ny][nx] = ' '
            val v = board2Id(mutableBoard.map { it.joinToString("") })
            neighbours.add(v)
        }
        neighbours
    }

    val targetBoard = listOf(
        "11111",
        "01111",
        "00 11",
        "00001",
        "00000"
    )
    val targetId = board2Id(targetBoard)
    val scanner = InputReader(inputStream)
    val n = scanner.nextInt()
    val ans = StringBuilder()
    repeat(n) {
        val startingBoard = mutableListOf<String>()
        repeat(5) {
            startingBoard.add(scanner.nextLine()!!)
        }
        val startId = board2Id(startingBoard)
        graph.bfs(startId, targetId, maxDepth = 10)
        if (graph.foundTarget()) {
            val distance = graph.distanceTo(targetId).toInt()
            ans.appendLine("Solvable in $distance move(s).")
        } else {
            ans.appendLine("Unsolvable in less than 11 move(s).")
        }
    }
    return ans.toString()
}
