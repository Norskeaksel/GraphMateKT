import graphMateKT.solutions.escapewallmaria


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class EscapewallmariaTest {

    @Test
    fun escapewallmariaa() {
        val expectedOutput = """2"""
        File("src/test/SampleInput/Escapewallmaria/input1").inputStream().use {
            assertThat(escapewallmaria(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun escapewallmariab() {
        val expectedOutput = """NOT POSSIBLE"""
        File("src/test/SampleInput/Escapewallmaria/input2").inputStream().use {
            assertThat(escapewallmaria(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun escapewallmariaSpeed() {
        val input = listOf("0 100 100") + List(100) { if (it == 0) "S" + "0".repeat(99) else "0".repeat(100) }
        val expectedOutput = """0"""
        makeStringsTestInput(input).use { input ->
            val time = measureTimeMillis {
                escapewallmaria(input)
            }
            debug("escapewallmaria time use: $time ms")
        }
    }
}