import graphMateKT.solutions.Graduation
        
import graphMateKT.INPUT
import graphMateKT._reader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import java.io.File

class GraduationTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }   

    @Test
    fun Graduationa() {
        val expectedOutput = 1
        _reader = File("src/test/SampleInput/Graduation/input1").inputStream().bufferedReader()
        assertThat(Graduation()).isEqualTo(expectedOutput)
    }

    @Test
    fun Graduationb() {
        val expectedOutput = 2
        _reader = File("src/test/SampleInput/Graduation/input2").inputStream().bufferedReader()
        assertThat(Graduation()).isEqualTo(expectedOutput)
    }

    @Test
    fun Graduationc() {
        val expectedOutput = 3
        _reader = File("src/test/SampleInput/Graduation/input3").inputStream().bufferedReader()
        assertThat(Graduation()).isEqualTo(expectedOutput)
    }

    @Test
    fun Graduationd() {
        val expectedOutput = 2
        _reader = File("src/test/SampleInput/Graduation/input4").inputStream().bufferedReader()
        assertThat(Graduation()).isEqualTo(expectedOutput)
    }

    @Test
    fun GraduationSpeed() {
        val expectedOutput = 1
        val gridSize = 50
        val row = "A".repeat(gridSize)
        val input = buildString {
            appendLine("1 1 1")
            repeat(gridSize) { appendLine(row) }
        }

        _reader = input.byteInputStream().bufferedReader()
        assertThat(Graduation()).isEqualTo(expectedOutput)
    }
}