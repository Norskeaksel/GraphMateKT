package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.graphClasses.Graph
import graphMateKT.graphClasses.Grid
import graphMateKT.graphClasses.IntGraph
import graphMateKT.graphics.graphGraphics.visualizeGraph
import graphMateKT.graphics.gridGraphics.visualizeGrid
import java.io.InputStream


private val TARGET_BOARD = arrayOf(
    charArrayOf('1', '1', '1', '1', '1'),
    charArrayOf('0', '1', '1', '1', '1'),
    charArrayOf('0', '0', ' ', '1', '1'),
    charArrayOf('0', '0', '0', '0', '1'),
    charArrayOf('0', '0', '0', '0', '0')
)

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

internal tailrec fun bitcode2Rank(bitCode: CharArray, nrOfZeros: Int, bitIndex: Int = 0, rankSoFar: Int = 0): Int =
    when {
        nrOfZeros == 0 || bitIndex >= bitCode.size -> rankSoFar
        bitCode[bitIndex] == '0' -> bitcode2Rank(bitCode, nrOfZeros - 1, bitIndex + 1, rankSoFar)
        else -> {
            val remainingBits = bitCode.lastIndex - bitIndex
            val newRankSoFar = rankSoFar + nrOfCombinations(remainingBits, nrOfZeros - 1)
            bitcode2Rank(bitCode, nrOfZeros, bitIndex + 1, newRankSoFar)
        }
    }

private fun boardsEmptyCoordinates(board: Array<CharArray>): Pair<Int, Int> {
    val y = board.indexOfFirst { row -> row.contains(' ') }
    val x = board[y].indexOf(' ')
    return x to y
}


internal fun board2Id(board: Array<CharArray>): Int {
    val bitCode = CharArray(24) // The 25 tiles minus the empty square.
    var emptyTileId = 0
    var bitIndex = 0
    board.forEachIndexed { y, row ->
        row.forEachIndexed { x, tile ->
            if (tile == ' ') emptyTileId = Tile(x, y).idGivenWidth(5)
            else bitCode[bitIndex++] = tile
        }
    }
    return bitcode2Rank(bitCode, 12) + KNIGHT_ORDERS * emptyTileId
}

internal fun rank2Bitcode(rank: Int, nrOfZeros: Int): CharArray {
    val nrOfBits = nrOfZeros * 2
    var remainingZeros = nrOfZeros
    var remainingRank = rank
    return CharArray(nrOfBits) { bitIndex ->
        val remainingBits = nrOfBits - bitIndex - 1
        val remainingZeroCombinations = nrOfCombinations(remainingBits, remainingZeros - 1)
        if (remainingRank < remainingZeroCombinations) '0'.also { remainingZeros-- }
        else '1'.also { remainingRank -= remainingZeroCombinations }
    }
}

internal fun id2Board(id: Int): Array<CharArray> {
    val emptyTileId = id / KNIGHT_ORDERS
    val bitCode = rank2Bitcode(id % KNIGHT_ORDERS, 12)
    return Array(5) { y ->
        CharArray(5) { x ->
            val tileId = x + y * 5
            when {
                tileId == emptyTileId -> ' '
                tileId < emptyTileId -> bitCode[tileId]
                else -> bitCode[tileId - 1]
            }
        }
    }
}

fun getNeighbours(board: Any): List<Array<CharArray>> {
    board as Array<CharArray>
    val (x, y) = boardsEmptyCoordinates(board)
    val neighbours = mutableListOf<Array<CharArray>>()
    dx.indices.forEach { i ->
        val nx = x + dx[i]
        val ny = y + dy[i]
        if (nx !in 0..4 || ny !in 0..4) return@forEach
        val mutableBoard = Array(5) { board[it].copyOf() }
        mutableBoard[y][x] = board[ny][nx]
        mutableBoard[ny][nx] = ' '
        neighbours.add(mutableBoard)
    }
    return neighbours
}


/** Solves https://open.kattis.com/problems/knightsfen */
internal fun knightsfen(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val n = scanner.nextInt()
    val ans = StringBuilder()
    val targetId = board2Id(TARGET_BOARD)
    val intGraph = IntGraph(NR_OF_COMBINATIONS)
    intGraph.connectWithRule { id ->
        getNeighbours(id2Board(id)).map { board2Id(it) }
    }
    intGraph.bfs(targetId, maxDepth = 9)
    repeat(n) {
        val startingBoard = Array(5) { CharArray(5) { ' ' } }
        repeat(5) { y ->
            val line = scanner.nextLine()!!
            line.forEachIndexed { x, c ->
                startingBoard[y][x] = c
            }
        }
        val distance = intGraph.distanceTo(board2Id(startingBoard))
        if (distance <= 10) {
            ans.appendLine("Solvable in ${distance.toInt()} move(s).")
        } else {
            ans.appendLine("Unsolvable in less than 11 move(s).")
        }
    }
    return ans.toString()
}
