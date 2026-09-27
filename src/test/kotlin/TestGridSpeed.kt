import graphMateKT.debug
import graphMateKT.graphClasses.Grid
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class TestGridSpeed {
    @Test
    fun testGridConnect() {
        val connectionTimes = mutableListOf<Long>()
        repeat(10) {
            val grid = Grid(1000, 1000)
            measureTimeMillis {
                grid.connectGrid { grid.getStraightNeighbours(it) }
            }.let { connectionTimes.add(it) }
        }
        debug("Grid connection times: $connectionTimes ms. Average time: ${connectionTimes.average()}")
    }

    @Test
    fun testGridConnectDefault() {
        val connectionTimes = mutableListOf<Long>()
        repeat(10) {
            val grid = Grid(1000, 1000)
            measureTimeMillis {
                grid.connectGridDefault()
            }.let { connectionTimes.add(it) }
        }
        debug("Grid default connection times: $connectionTimes ms. Average time: ${connectionTimes.average()}")
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
        debug("Grid connection times: $connectionTimes ms. Average time: ${connectionTimes.average()}")
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
        debug("Grid default connection times: $connectionTimes ms. Average time: ${connectionTimes.average()}")
    }
}
