package graphMateKT.solutions

import graphMateKT.graphClasses.Graph
import graphMateKT.readInt
import graphMateKT.readString

internal fun main() {
    val ans = importspaghetti()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/importspaghetti */
internal fun importspaghetti(): String {
    val n = readInt()
    val g = Graph()
    val files = readString().split(" ")
    files.forEach { file ->
        g.addNode(file)
    }
    repeat(n) {
        val (file, importLines) = readString().split(" ")
        repeat(importLines.toInt()) {
            val imports = readString().replace("import ", "").split(", ")
            imports.forEach { g.addEdge(file, it) }
        }
    }
    var shortestCycleLength = Int.MAX_VALUE
    var shortestCyclePath = listOf<Any>()

    g.nodes().reversed().forEach { node ->
        g.bfs(node, node)
        if(g.foundTarget() && g.depth() < shortestCycleLength) {
            shortestCycleLength = g.depth()
            shortestCyclePath = g.getPath(node)
        }
    }
    if(shortestCycleLength == Int.MAX_VALUE) return "SHIP IT"
    return shortestCyclePath.joinToString().replace(", ", " ")
}
