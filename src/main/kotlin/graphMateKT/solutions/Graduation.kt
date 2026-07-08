package graphMateKT.solutions

import graphMateKT.graphClasses.Grid
import graphMateKT.readInts
import graphMateKT.readLines


internal fun main() {
    val ans = Graduation()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/Graduation */
internal fun Graduation(): Int {
    readInts(3)
    val grid = Grid(readLines())
    val dataGroups = grid.nodes().groupBy { it.data }
    val columGroups = grid.nodes().groupBy { it.x }
    grid.connectGrid { t ->
        dataGroups[t.data]!! + columGroups[t.x]!!
    }
    val components = grid.stronglyConnectedComponents()
    return components.size
}
