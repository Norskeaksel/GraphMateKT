package graphMateKT.solutions

import graphMateKT.graphClasses.IntGraph
import graphMateKT.readInts

internal fun main() {
    val ans = brexit()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/brexit */
internal fun brexit(): String {
    val (c, p, x, l) = readInts(4)
    val g = IntGraph(c + 1, p * 2)
    repeat(p) {
        val (u, v) = readInts(2)
        g.connect(u, v)
    }
    if (x == l)
        return "leave"
    return stayOrLeave(g, x, l)
}

private fun stayOrLeave(g: IntGraph, x: Int, l: Int): String {
    val leavers = BooleanArray(g.size())
    leavers[l] = true
    val q = ArrayDeque<Int>()
    q.add(l)
    val finalPath = mutableListOf(x)
    var loopTime = 0L
    while (q.isNotEmpty()) {
        val loopStartTime = System.currentTimeMillis()
        val c = q.removeFirst()
        val n = g.neighbours(c)
        n.forEach { u ->
            if (leavers[u] || u == x)
                return@forEach
            val nn = g.neighbours(u)
            if (nn.count { v -> leavers[v] } >= nn.size / 2.0) {
                q.add(u)
                finalPath.add(u)
                leavers[u] = true
            }
        }
        loopTime += System.currentTimeMillis() - loopStartTime
        if (loopTime > 2000L) break
    }
    // debug(finalPath)
    // g.visualize(bidirectional = true, finalPath=finalPath)
    return if (g.neighbours(x).count { v -> leavers[v] } >= g.neighbours(x).size / 2.0) "leave" else "stay"
}
