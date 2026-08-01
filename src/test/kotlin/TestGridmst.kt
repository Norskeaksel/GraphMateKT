import graphMateKT.solutions.gridmst


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.math.ceil
import kotlin.system.measureTimeMillis

class GridmstTest {

    @Test
    fun gridmsta() {
        val expectedOutput = """3"""
        File("src/test/SampleInput/Gridmst/input1").inputStream().use {
            assertThat(gridmst(it, 5)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun gridmstb() {
        val expectedOutput = """14"""
        File("src/test/SampleInput/Gridmst/input2").inputStream().use {
            assertThat(gridmst(it, 15)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun gridmstQuadrants() {
        val points = listOf(25 to 25, 25 to 75, 75 to 25, 75 to 75, 50 to 50)
        val input = listOf("${points.size}") + points.map { "${it.first} ${it.second}" }
        makeStringsTestInput(input).use { stream ->
            gridmst(stream, 100)
        }
    }

    @Test
    fun gridmstFivers() {
        val dim = 101
        val expectedOutput = "${10 * (dim / 5) * 2}"
        val points = List(ceil(dim / 5.0).toInt()) { listOf(it * 5 to it * 5, it * 5 to 100 - it * 5) }.flatten()
        val input = listOf("${points.size}") + points.map { "${it.first} ${it.second}" }
        makeStringsTestInput(input).use { stream ->
            assertThat(gridmst(stream, dim)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun gridmstSpeed0() {
        val input = listOf("1000") + List(1000) { "${it % 1000} ${it / 1000}" }
        val expectedOutput = """999"""
        makeStringsTestInput(input).use { input ->
            val time = measureTimeMillis {
                assertThat(gridmst(input)).isEqualTo(expectedOutput)
            }
            debug("gridmst time use: $time ms")
        }
    }

    @Test
    fun gridmstSpeed() {
        val input = listOf("100000") + List(100000) { "${it % 1000} ${it / 1000}" }
        val expectedOutput = """99999"""
        makeStringsTestInput(input).use { input ->
            val time = measureTimeMillis {
                assertThat(gridmst(input)).isEqualTo(expectedOutput)
            }
            debug("gridmst time use: $time ms")
        }
    }
}


