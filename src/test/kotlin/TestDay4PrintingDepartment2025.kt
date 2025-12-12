import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.examples.Day4PrintingDepartment2025
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import java.io.File
import org.junit.jupiter.api.Test

class Day4PrintingDepartmentTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }   


    @Test
    fun Day4PrintingDepartment2025a() {
        val expectedOutput = 13
        _reader = File("src/test/SampleInput/Day4PrintingDepartment2025/input1").inputStream().bufferedReader()
        assertThat(Day4PrintingDepartment2025()).isEqualTo(expectedOutput)
    }
}