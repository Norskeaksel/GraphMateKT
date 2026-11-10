package graphMateKT.solutions

import graphMateKT.graphClasses.Graph
import graphMateKT.graphClasses.IntGraph
import graphMateKT.readInt
import graphMateKT.readInts

internal fun main() {
    val ans = walkforest()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/walkforest */
internal fun walkforest(): String {
    val ans = StringBuilder()
    while (true) {
        val n = readInt()
        if (n == 0)
            break
        val m = readInt()
        val g = IntGraph(n + 1)
        repeat(m) {
            val (a, b, l) = readInts(3)
            g.connect(a, b, l)
        }
        g.shortestPathsFrom(2, 1)
        val dag = makeDAG(g)
        //g.visualize(true)
        //dag.visualize()
        ans.appendLine(dag.nrOfPaths(1, 2))
    }
    return ans.toString()
}

private fun makeDAG(g: IntGraph): Graph {
    val newG = Graph()
    newG.addNode(1)
    newG.addNode(2)
    val distances =  DoubleArray(g.size())
    g.nodes().forEach { distances[it] = g.distanceTo(it) }
    g.nodes().forEach { u ->
        g.edges(u).forEach { (w, v) ->
            if (distances[v] < distances[u])
                newG.addEdge(u, v, w)
        }
    }
    return newG
}
