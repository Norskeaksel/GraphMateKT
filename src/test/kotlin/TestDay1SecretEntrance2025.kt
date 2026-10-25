import graphMateKT.solutions.adventOfCode.day1SecretEntrance2025
        

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class Day1SecretEntrance2025Test {
    @Test
    fun Day1SecretEntrance2025a() {
        val expectedOutput = 6
        File("src/test/SampleInput/Day1SecretEntrance2025/input1").inputStream().use{
            assertThat(day1SecretEntrance2025(it)).isEqualTo(expectedOutput)
        }
    }
}