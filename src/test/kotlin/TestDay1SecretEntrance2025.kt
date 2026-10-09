import graphMateKT.solutions.Day1SecretEntrance2025
        

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class Day1SecretEntrance2025Test {

    @Test
    fun Day1SecretEntrance2025a() {
        val expectedOutput = """"""
        File("src/test/SampleInput/Day1SecretEntrance2025/input1").inputStream().use{
            assertThat(Day1SecretEntrance2025(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun Day1SecretEntrance2025Speed() {
        val input = listOf("1") + List(100) { "1".repeat(100) }
        val expectedOutput = """"""
        makeStringsTestInput(input).use { line ->
            val time = measureTimeMillis {
                assertThat(Day1SecretEntrance2025(line)).isEqualTo(expectedOutput)
            }
            debug("Day1SecretEntrance2025 time use: $time ms")
        }
    }
}