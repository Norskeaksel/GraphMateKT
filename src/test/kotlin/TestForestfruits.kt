import graphMateKT.solutions.forestfruits


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class ForestfruitsTest {

    @Test
    fun forestfruitsa() {
        val expectedOutput = 4L
        File("src/test/SampleInput/Forestfruits/input1").inputStream().use {
            assertThat(forestfruits(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun forestfruitsb() {
        val expectedOutput = -1L
        File("src/test/SampleInput/Forestfruits/input2").inputStream().use {
            assertThat(forestfruits(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun forestfruitsd() {
        val expectedOutput = -1L
        File("src/test/SampleInput/Forestfruits/inputSpeed").inputStream().use {
            assertThat(forestfruits(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun forestfruitsSpeed() {
        val input = listOf("20000", "100000", "20000", "20000", "2000000000") + List(100_000) {
            "$1 ${(it + 1) % 20_001} 1"
        } + List(20_000) { "${it + 1}" }
        val expectedOutput = 2L
        makeStringsTestInput(input).use { input ->
            val time = measureTimeMillis {
                assertThat(forestfruits(input)).isEqualTo(expectedOutput)
            }
            debug("forestfruits time use: $time ms")
        }
    }
}