package graphMateKT.solutions.adventOfCode

import fastInputReader.InputReader
import graphMateKT.debug
import graphMateKT.graphClasses.Graph
import me.tongfei.progressbar.ProgressBar
import java.io.InputStream

internal fun main() {
    val ans = Day23LANParty2024Part2(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/Day23LANParty2024 */
internal fun Day23LANParty2024Part1(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val graph = Graph()
    generateSequence { scanner.nextString() }.forEach { line ->
        val (u, v) = line.split("-")
        graph.connect(u, v)
    }
    // graph.visualizeGraph(true)
    val groupsOfThreeWithT = mutableSetOf<Triple<String, String, String>>()
    graph.nodes().forEach { a ->
        a as String
        if (a.first() != 't') return@forEach
        graph.neighbours(a).forEach { b ->
            b as String
            graph.neighbours(b).forEach { c ->
                c as String
                if (a in graph.neighbours(c)) {
                    val order = listOf(a, b, c).sorted()
                    groupsOfThreeWithT.add(Triple(order[0], order[1], order[2]))
                }
            }
        }
    }
    debug("groupsOfThreeWithT: $groupsOfThreeWithT")
    return groupsOfThreeWithT.size.toString()
}

internal fun Day23LANParty2024Part2(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val graph = Graph()
    generateSequence { scanner.nextString() }.forEach { line ->
        val (u, v) = line.split("-")
        graph.connect(u, v)
    }
    val potentialGroups = graph.nodes().map { listOf(it) + graph.neighbours(it) }
    var biggestGroup = listOf<Any>()
    ProgressBar.wrap(potentialGroups, "Part 2").forEach outer@{ group ->
        val subsets = group.subsets()
        subsets.forEach { subset ->
            if (isFullyConnected(graph, subset.toSet())) {
                if (subset.size > biggestGroup.size)
                    biggestGroup = subset
            }
        }
    }
    return biggestGroup.sortedBy { it.toString() }.joinToString(",")
}

internal fun List<Any>.subsets(): List<List<Any>> = fold(listOf(emptyList())) { subsetsSoFar, node ->
    val nextSubsets = subsetsSoFar.map { it + node }
    subsetsSoFar + nextSubsets
}

internal fun isFullyConnected(graph: Graph, group: Set<Any>): Boolean {
    group.forEach { u ->
        val uSet = (listOf(u) + graph.neighbours(u)).toSet()
        if (!uSet.containsAll(group))
            return false
    }
    return true
}