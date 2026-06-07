/*import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.solutions.speedyescape
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import java.io.File
import org.junit.jupiter.api.Test

class SpeedyescapeTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }   

    @Test
    fun speedyescapea() {
        val expectedOutput = -1.0
        _reader = File("src/test/SampleInput/Speedyescape/input1").inputStream().bufferedReader()
        assertThat(speedyescape()).isEqualTo(expectedOutput)
    }


    @Test
    fun speedyescapeb() {
        val expectedOutput = 74.6666666667
        _reader = File("src/test/SampleInput/Speedyescape/input2").inputStream().bufferedReader()
        assertThat(speedyescape()).isBetween(expectedOutput-1e-6, expectedOutput+1e-6)
    }


    @Test
    fun speedyescapec() {
        val expectedOutput = 137.142857143
        _reader = File("src/test/SampleInput/Speedyescape/input3").inputStream().bufferedReader()
        assertThat(speedyescape()).isBetween(expectedOutput-1e-6, expectedOutput+1e-6)
    }


    @Test
    fun speedyescapee() {
        val expectedOutput = 213.3333333
        _reader = File("src/test/SampleInput/Speedyescape/input5").inputStream().bufferedReader()
        assertThat(speedyescape()).isBetween(expectedOutput-1e-6, expectedOutput+1e-6)
    }
}*/