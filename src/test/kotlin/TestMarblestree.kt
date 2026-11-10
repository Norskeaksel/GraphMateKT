import graphMateKT.solutions.marblestree


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class MarblestreeTest {

    @Test
    fun marblestreea() {
        val expectedOutput = """7
14
20
"""
        File("src/test/SampleInput/Marblestree/input1").inputStream().use {
            assertThat(marblestree(it)).isEqualTo(expectedOutput)
        }
    }

    /*@Test
    fun marblestreeSpeed() {
        val input = listOf("1") + List(100) { "1".repeat(100) }
        val expectedOutput = """"""
        makeStringsTestInput(input).use { line ->
            val time = measureTimeMillis {
                assertThat(marblestree(line)).isEqualTo(expectedOutput)
            }
            debug("marblestree time use: $time ms")
        }
    }*/
}