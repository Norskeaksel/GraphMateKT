import graphMateKT.solutions.gridmst


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class GridmstTest {

    @Test
    fun gridmsta() {
        val expectedOutput = """3"""
        File("src/test/SampleInput/Gridmst/input1").inputStream().use {
            assertThat(gridmst(it, 2, 2)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun gridmstb() {
        val expectedOutput = """14"""
        File("src/test/SampleInput/Gridmst/input2").inputStream().use {
            assertThat(gridmst(it, 13, 3)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun gridmstc() {
        val expectedOutput = """4"""
        File("src/test/SampleInput/Gridmst/input3").inputStream().use {
            assertThat(gridmst(it, 3, 3)).isEqualTo(expectedOutput)
        }
    }

    /*@Test
    fun gridmstSpeed() {
        val input = listOf("100000") + List(100_000) { "${it / 100} ${it / 100}" }
        val expectedOutput = """1998"""
        makeStringsTestInput(input).use { input ->
            val time = measureTimeMillis {
                assertThat(gridmst(input)).isEqualTo(expectedOutput)
            }
            debug("gridmst time use: $time ms")
        }
    }*/
}