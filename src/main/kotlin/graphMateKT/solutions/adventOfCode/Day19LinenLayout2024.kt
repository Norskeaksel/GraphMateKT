package graphMateKT.solutions.adventOfCode

import fastInputReader.InputReader
import graphMateKT.graphClasses.Graph
import graphMateKT.graphics.graphGraphics.visualizeGraph
import java.io.InputStream

internal fun main() {
    val ans = Day19LinenLayout2024(System.`in`, 2)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/Day19LinenLayout2024 */
internal fun Day19LinenLayout2024(inputStream: InputStream, part: Int): String {
    val scanner = InputReader(inputStream)
    val patterns = scanner.nextLine()!!.split(", ")
    scanner.nextLine()
    val targetWords = generateSequence { scanner.nextString() }.toList()
    var ans = 0L
    targetWords.forEach { target ->
        val graph = Graph()
        graph.addNode("")
        graph.connectWithRule { prefix ->
            patterns.map { prefix as String + it }.filter {
                it == target.substring(0 until it.length.coerceAtMost(target.length))
            }
        }
        graph.dfs("")
        if (target in graph.visitedNodes()) {
            ans += if (part == 1) 1 else graph.nrOfPaths("", target)
        }
        //graph.visualizeGraph()
    }
    return ans.toString()
}
