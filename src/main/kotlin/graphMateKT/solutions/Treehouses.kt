package graphMateKT.solutions

import graphMateKT.graphClasses.Graph
import graphMateKT.readDoubles
import graphMateKT.readInts
import kotlin.math.pow
import kotlin.math.sqrt

internal fun main() {
    val ans = treehouses()
    println(ans)
    System.out.flush()
}

internal data class Point(val x: Double, val y: Double)

/** Solves https://open.kattis.com/problems/treehouses */
internal fun treehouses(): Double {
    val (n, e, p) = readInts(3)
    val g = Graph()
    val t8s = mutableListOf<Point>()
    repeat(n) {
        val (x, y) = readDoubles(2)
        val node = Point(x, y)
        t8s.add(node)
        g.addNode(node)
    }
    for (i in 0..<g.size()) {
        for (j in (i + 1)..<g.size()) {
            val u = t8s[i]
            val v = t8s[j]
            val weight = sqrt((v.x - u.x).pow(2.0) + (v.y - u.y).pow(2.0))
            g.connect(u, v, weight)
        }
    }
    repeat(e) {
        g.connect(t8s[it], t8s[e - 1], 0.0)
    }
    repeat(p) {
        val (u, v) = readInts(2)
        g.connect(t8s[u-1], t8s[v-1], 0.0)
    }
    val ans = g.minimumSpanningTree()
    return ans.first
}
