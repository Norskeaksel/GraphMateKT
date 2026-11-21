package graphMateKT.solutions.adventOfCode

import fastInputReader.InputReader
import graphMateKT.graphClasses.Grid
import java.io.InputStream

internal fun main() {
    val ans = Day25CodeChronicle2024(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/Day25CodeChronicle2024 */
internal fun Day25CodeChronicle2024(inputStream: InputStream): Int {
    val scanner = InputReader(inputStream)
    val input = generateSequence { scanner.nextString() }.toList()
    val blocks = input.chunked(7)
    val locks = blocks.filter { it.first().all { it == '#' } }
    val keys = blocks.filter { it.last().all { it == '#' } }
    var ans = 0
    locks.forEach { lock ->
        val lockCells = Grid(lock).nodes().filter { it.data == '#' }.toSet()
        keys.forEach { key ->
            val keyCells = Grid(key).nodes().filter { it.data == '#' }.toSet()
            if (lockCells.intersect(keyCells).isEmpty()) {
                ans++
            }
        }
    }
    return ans
}
