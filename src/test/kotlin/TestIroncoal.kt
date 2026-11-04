import graphMateKT.solutions.ironcoal


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.math.min
import kotlin.system.measureTimeMillis

class IroncoalTest {

    @Test
    fun ironcoala() {
        val expectedOutput = """2"""
        File("src/test/SampleInput/Ironcoal/input1").inputStream().use {
            assertThat(ironcoal(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun ironcoalb() {
        val expectedOutput = """impossible"""
        File("src/test/SampleInput/Ironcoal/input2").inputStream().use {
            assertThat(ironcoal(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun ironcoalc() {
        val expectedOutput = """4"""
        File("src/test/SampleInput/Ironcoal/input3").inputStream().use {
            assertThat(ironcoal(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun ironcoalSpeed() {
        val (n, k) = listOf(10000, 1)
        val input =
            listOf("$n ${n - 2} $k") + List(n - 2) { "${it + 2}" } + listOf("$n") + List(n) { "1 ${min(it + 2, n)}" }
        val expectedOutput = """9999"""
        makeStringsTestInput(input).use { line ->
            val time = measureTimeMillis {
                assertThat(ironcoal(line)).isEqualTo(expectedOutput)
            }
            debug("ironcoal time use: $time ms")
        }
    }
}