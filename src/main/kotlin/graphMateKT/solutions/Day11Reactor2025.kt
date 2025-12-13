package graphMateKT.solutions

import graphMateKT.graphClasses.Graph
import graphMateKT.graphGraphics.visualizeGraph

internal fun main() {
    val input = generateSequence { readlnOrNull() }.toList()
    println(Day11Reactor2025Part1(input))
    println(Day11Reactor2025Part2(input))
    System.out.flush()
}

/** Solves https://adventofcode.com/2025/day/11 */
internal fun Day11Reactor2025Part1(input: List<String>): Long {
    val g = Graph(false)
    input.forEach { line ->
        val (from, to) = line.split(": ")
        to.split(" ").forEach { neighbor ->
            g.addEdge(from, neighbor)
        }
    }
    return g.nrOfPaths("you", "out")
}

/** https://adventofcode.com/2025/day/11#part2 */
internal fun Day11Reactor2025Part2(input: List<String>): Long {
    val g = Graph(false)
    val start = "svr"
    val dac = "dac"
    val fft = "fft"
    val end = "out"
    input.forEach { line ->
        val (from, to) = line.split(": ")
        to.split(" ").forEach { neighbor ->
            g.addEdge(from, neighbor)
        }
    }
    g.bfs(dac, fft)
    val firstNode = if (g.foundTarget()) dac else fft
    val secondNode = if (firstNode == dac) fft else dac
    //g.resetSearchResults()
    //g.visualizeGraph(finalPath = listOf(start, firstNode, secondNode, end))
    return g.nrOfPaths(start, firstNode) * g.nrOfPaths(firstNode, secondNode) * g.nrOfPaths(secondNode, end)
}
