package graphMateKT.solutions
import graphMateKT.graphClasses.Grid
import graphMateKT.Tile

/** Solves https://adventofcode.com/2024/day/18 */
internal fun day18aRAMRun2024(input: List<String>, gridSize: Int, lineCount: Int): Int {
    val grid = Grid(gridSize, gridSize)
    for((i,line) in input.withIndex()){
        if (i >= lineCount)
            break
        val (x, y) = line.split(",").map { it.toInt() }
        grid.deleteNodeAtXY(x, y)
    }
    grid.connectGridDefault()
    val goal = grid.xy2Node(gridSize - 1, gridSize - 1)!!
    grid.bfs(Tile(0,0), goal)
    val ans = grid.distanceTo(goal).toInt()
    // grid.visualizeSearch()
    return ans
}
