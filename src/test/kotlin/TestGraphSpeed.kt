import fastInputReader.InputReader
import graphMateKT.Tile
import graphMateKT.debug
import graphMateKT.graphClasses.Graph
import graphMateKT.graphClasses.Grid
import graphMateKT.graphClasses.IntGraph
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class TestGraphSpeed {
    @Test
    fun testGraphBfsSpeed() {
        makeGraphTestInput(testCases = 5, nodes = 1_000_000, edges = 1_000_000).use { inputStream ->
            val scanner = InputReader(inputStream)
            val n = scanner.nextLine()!!.toInt()
            repeat(n) {
                measureTimeMillis {
                    val (_, edges) = scanner.nextLine()!!.split(" ").map { it.toInt() }
                    val graph = Graph()
                    repeat(edges) {
                        val (u, v) = scanner.nextLine()!!.split(" ").map { it.toInt() }
                        graph.addEdge(u - 1, v - 1)
                    }
                    try {
                        graph.bfs(1)
                    } catch (e: IllegalStateException) {
                        debug(e)
                    }

                }.also { debug("Graph time: $it ms") }
            }
        }
    }

    @Test
    fun testIntGraphBfsSpeed() {
        makeGraphTestInput(testCases = 5, nodes = 1_000_000, edges = 1_000_000).use { inputStream ->
            val scanner = InputReader(inputStream)
            val n = scanner.nextLine()!!.toInt()
            repeat(n) {
                measureTimeMillis {
                    val (nodes, edges) = scanner.nextLine()!!.split(" ").map { it.toInt() }
                    val graph = IntGraph(nodes)
                    repeat(edges) {
                        val (u, v) = scanner.nextLine()!!.split(" ").map { it.toInt() }
                        graph.addEdge(u - 1, v - 1)
                    }
                    graph.bfs(1)
                }.also { debug("intGraph time: $it ms") }
            }
        }
    }

    @Test
    fun testGridBfsSpeedDefaultConnect() {
        debug("Connect Grid de")
        repeat(5) {
            measureTimeMillis {
                val graph = Grid(1000, 1000)
                graph.connectGridDefault()
                graph.bfs(Tile(0, 0, null))
            }.also { debug("Grid time: $it ms") }
        }
    }
}