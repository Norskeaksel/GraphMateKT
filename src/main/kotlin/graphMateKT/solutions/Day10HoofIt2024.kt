package graphMateKT.solutions
import graphMateKT.graphClasses.Grid

/** Solves https://adventofcode.com/2024/day/10 */
internal fun day10aHoofIt2024(input: List<String>): Long {
    var ans = 0L
    val grid = Grid(input)
    grid.print()
    grid.nodes().forEach { t ->
        grid.getStraightNeighbours(t).forEach { n ->
            if (n.data == t.data as Char + 1)
                grid.addEdge(t, n)
        }
    }
    grid.nodes().forEach { it ->
        if (it.data != '0')
            return@forEach
        grid.dfs(it)
        val visitedNines = grid.currentVisitedNodes().count { it.data == '9' }
        ans += visitedNines
    }
    return ans
}