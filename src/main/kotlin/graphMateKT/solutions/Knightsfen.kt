package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.debug
import graphMateKT.graphClasses.Grid
import graphMateKT.graphClasses.IntGraph
import java.io.InputStream
import kotlin.system.measureTimeMillis

const val nrOfPermutations = 67_603_900 // 25! / (12! * 12!)

internal fun main() {
    val ans = knightsfen(System.`in`)
    println(ans)
    System.out.flush()
}

private fun boardsEmptyCoordinates(board: List<String>): String {
    repeat(5) { y ->
        repeat(5) { x ->
            if (board[y][x] == ' ')
                return (x + y * 5).toString(2).padStart(5, '0')
        }
    }
    return ""
}

fun board2Id(board: List<String>): Int {
    val bitCode = board.joinToString().replace(" ", "1") + boardsEmptyCoordinates(board)
    return bitCode.toInt(2)
}

fun id2Board(id: Int) = id.toString(2).padStart(30, '0')

/** Solves https://open.kattis.com/problems/knightsfen */
internal fun knightsfen(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    // val n = scanner.nextInt()
    val targetBoard = listOf(
        "11111",
        "01111",
        "00 11",
        "00001",
        "00000"
    )

    val refrenceGrid = Grid(targetBoard)
    refrenceGrid.connectWithRule { t ->
        val dx = intArrayOf(-2, -1, 1, 2, 2, 1, -1, -2)
        val dy = intArrayOf(-1, -2, -2, -1, 1, 2, 2, 1)
        dx.indices.mapNotNull { i -> refrenceGrid.xy2NodeOrNull(t.x + dx[i], t.y + dy[i]) }
    }
    measureTimeMillis {
        val graph = IntGraph(nrOfPermutations)
    }.also { debug("Initialization took $it ms") }
    /*repeat(n) {
        val startingBoard = mutableListOf<String>()
        repeat(5) {
            startingBoard.add(scanner.nextString()!!)
        }
    }*/
    return ""
}
