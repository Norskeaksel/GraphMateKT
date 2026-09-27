import graphMateKT.debug
import graphMateKT.graphClasses.Grid
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class TestGridSpeed {
    /*@Test
    fun testGridConnect() {
        repeat(5) {
            val grid = Grid(1000, 1000)
            measureTimeMillis {
                grid.connectGrid { grid.getStraightNeighbours(it) }
            }.also { debug("Grid connection times: $it ms.") }
        }
    }

    @Test
    fun testGridConnectDefault() {
        val connectionTimes = mutableListOf<Long>()
        repeat(5) {
            val grid = Grid(1000, 1000)
            measureTimeMillis {
                grid.connectGridDefault()
            }.also {
                debug("Grid default connection times: $it ms.")
            }
        }
    }

    @Test
    fun testGridConnectMany() {
        val connectionTimes = mutableListOf<Long>()
        repeat(100) {
            measureTimeMillis {
                val grid = Grid(100, 100)
                grid.connectGrid { grid.getStraightNeighbours(it) }
            }.let { connectionTimes.add(it) }
        }
        debug("Grid default average connection time: ${connectionTimes.average()} ms.")
    }

    @Test
    fun testGridConnectDefaultMany() {
        val connectionTimes = mutableListOf<Long>()
        repeat(100) {
            measureTimeMillis {
                val grid = Grid(100, 100)
                grid.connectGridDefault()
            }.let { connectionTimes.add(it) }
        }
        debug("Grid default average connection time: ${connectionTimes.average()} ms.")
    }*/
}
