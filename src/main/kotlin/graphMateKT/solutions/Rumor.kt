package graphMateKT.solutions

import graphMateKT.graphClasses.IntGraph
import graphMateKT.readInts

/** Solves https://codeforces.com/problemset/problem/893/C */
internal fun rumor(): Long {
    val (n, m) = readInts(2)
    val g = IntGraph(n+1, m * 2)
    val c = listOf(0) + readInts(n)
    repeat(m) {
        val (x, y) = readInts(2)
        g.connect(x, y)
    }
    val components = g.stronglyConnectedComponents()
    System.err.println(components)
    var sum = 0L
    components.forEach { component ->
        val min = component.minOf { c[it] }
        sum += min
    }
    return sum
}

internal fun main() {
    println(rumor())
}
