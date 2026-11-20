import graphMateKT.solutions.adventOfCode.Day19LinenLayout2024


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class Day19LinenLayout2024Test {

    @Test
    fun Day19LinenLayout2024Part1() {
        val expectedOutput = "6"
        File("src/test/SampleInput/Day19LinenLayout2024/input1").inputStream().use {
            assertThat(Day19LinenLayout2024(it, 1)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun Day19LinenLayout2024Part2() {
        val expectedOutput = "16"
        File("src/test/SampleInput/Day19LinenLayout2024/input1").inputStream().use {
            assertThat(Day19LinenLayout2024(it, 2)).isEqualTo(expectedOutput)
        }
    }
}