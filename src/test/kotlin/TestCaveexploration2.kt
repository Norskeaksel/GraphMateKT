import graphMateKT.solutions.caveexploration2


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class Caveexploration2Test {

    @Test
    fun caveexploration2a() {
        val expectedOutput = 4
        File("src/test/SampleInput/Caveexploration2/input1").inputStream().use {
            assertThat(caveexploration2(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun caveexploration2b() {
        val expectedOutput = 12
        File("src/test/SampleInput/Caveexploration2/input2").inputStream().use {
            assertThat(caveexploration2(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun caveexploration2Speed() {
        val expectedOutput = 99999999
        val lines = listOf("100") + List(100) { if (it == 0) "0 ".repeat(100) else "$expectedOutput ".repeat(100) }
        makeStringsTestInput(lines).use { input ->
            val time = measureTimeMillis {
                assertThat(caveexploration2(input)).isEqualTo(expectedOutput)
            }
            debug("caveexploration2 time use: $time ms")
        }
    }
}