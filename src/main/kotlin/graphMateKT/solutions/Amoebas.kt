package graphMateKT.solutions

import graphMateKT.graphClasses.Grid
import graphMateKT.readInts
import graphMateKT.readString

internal fun main() {
    val ans = amoebas()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/amoebas */
internal fun amoebas(): String {
    val (m,_) = readInts(2)
    val lines = mutableListOf<String>()
    repeat(m){
        lines.add(readString())
    }
    val grid = Grid(lines)
    grid.deleteNodesWithData('.')
    grid.connectWithRule { t->
        grid.getAllNeighbours(t)
    }
    return grid.stronglyConnectedComponents().size.toString()
}
