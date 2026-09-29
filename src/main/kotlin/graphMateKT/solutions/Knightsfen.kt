package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphAlgorithms.BFS
import graphMateKT.graphClasses.AdjacencyList
import java.io.InputStream

/* internal fun main() {
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

fun board2BitCode(board: List<String>): String {
    return board.joinToString().replace(" ", "1") + boardsEmptyCoordinates(board)
}

class KnightGridAdjacencyList : AdjacencyList {
    val dx = listOf(-2, -1, 1, 2).let { it + it }
    val dy = listOf(-1, -2, -2, -1, 1, 2, 2, 1)
    override fun forEachNeighbour(node: Int, action: (Int) -> Unit) {
        val neighbours =
            action(node)
    }

    override fun forEachEdge(node: Int, action: (Double, Int) -> Unit) {}

    override val size = 10_000_000
}

/** Solves https://open.kattis.com/problems/knightsfen */
internal fun knightsfen(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val n = scanner.nextInt()
    val targetBoard = """11111
        01111
        00 11
        00001
        00000
    """.trimIndent()
    repeat(n) {
        val startingBoard = mutableListOf<String>()
        repeat(5) {
            startingBoard.add(scanner.nextString()!!)
        }
        val adjacencyList = KnightGridAdjacencyList()
        val bfs = BFS(adjacencyList)
        bfs.bfs(listOf(1))
    }
    return ""
}*/
