package graphMateKT.examples

import graphMateKT.graphClasses.Grid
import graphMateKT.readString


internal fun main() {
    val ans = Day4PrintingDepartment2025()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/Day4PrintingDepartment */
internal fun Day4PrintingDepartment2025(): Int {
    val input = generateSequence { readString() }
    val grid = Grid(input.toList())
    return grid.nodes().count { node -> node.data == '@' && grid.getAllNeighbours(node).count { it.data == '@' } < 4 }
}
