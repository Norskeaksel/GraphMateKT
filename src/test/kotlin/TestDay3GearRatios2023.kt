import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.solutions.adventOfCode.day3GearRatios2023
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import java.io.File
import org.junit.jupiter.api.Test

class Day3GearRatios2023Test {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }   

    @Test
    fun day3GearRatios2023a() {
        val expectedOutput = 4361
        _reader = File("src/test/SampleInput/Day3GearRatios2023/input1").inputStream().bufferedReader()
        assertThat(day3GearRatios2023()).isEqualTo(expectedOutput)
    }

}