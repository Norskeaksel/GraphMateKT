package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.IntGraph
import java.io.InputStream

internal fun main() {
    val ans = promotions(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/promotions */
internal fun promotions(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val (a, b, e, p) = scanner.nextIntArray(4)
    val ancestorsGraph = IntGraph(e, p)
    val descendantsGraph = IntGraph(e, p)
    repeat(p) {
        val u = scanner.nextInt()
        val v = scanner.nextInt()
        descendantsGraph.addEdge(u, v)
        ancestorsGraph.addEdge(v, u)
    }
    val ancestorOrder = ancestorsGraph.topologicalSort()
    val descendantsOrder = ancestorOrder.reversed()
    // ancecstorsGraph.visualizeGraph(startPaused = true)
    val ancestorsOfNode = Array(e) { mutableSetOf<Int>() }
    val descendantsOfNode = Array(e) { mutableSetOf<Int>() }
    ancestorOrder.forEach { u ->
        ancestorsGraph.forEachNeighbour(u) { v ->
            ancestorsOfNode[u].add(v)
            ancestorsOfNode[u].addAll(ancestorsOfNode[v])
        }
    }
    descendantsOrder.forEach { u ->
        descendantsGraph.forEachNeighbour(u) { v ->
            descendantsOfNode[u].add(v)
            descendantsOfNode[u].addAll(descendantsOfNode[v])
        }
    }
    val nrOfGuaranteedPeopleA = descendantsOfNode.count { e - it.size <= a }
    val nrOfGuaranteedPeopleB = descendantsOfNode.count {  e - it.size <= b }
    val nrOfUnpromotablePeople = ancestorsOfNode.count { it.size >= b }
    return """$nrOfGuaranteedPeopleA
$nrOfGuaranteedPeopleB
$nrOfUnpromotablePeople
"""
}
