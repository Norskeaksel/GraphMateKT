import graphMateKT.solutions.noway

import graphMateKT.INPUT
import graphMateKT._reader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import java.io.File
import org.junit.jupiter.api.Test

class NowayTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }

    @Test
    fun nowaya() {
        val expectedOutput = 2L
        _reader = File("src/test/SampleInput/Noway/input1").inputStream().bufferedReader()
        assertThat(noway()).isEqualTo(expectedOutput)
    }


    @Test
    fun nowayb() {
        val expectedOutput = 3L
        _reader = File("src/test/SampleInput/Noway/input2").inputStream().bufferedReader()
        assertThat(noway()).isEqualTo(expectedOutput)
    }


    @Test
    fun nowayc() {
        val expectedOutput = 4L
        _reader = File("src/test/SampleInput/Noway/input3").inputStream().bufferedReader()
        assertThat(noway()).isEqualTo(expectedOutput)
    }

    @Test
    fun nowayd() {
        val expectedOutput = 361294640L
        _reader = File("src/test/SampleInput/Noway/input4").inputStream().bufferedReader()
        assertThat(noway()).isEqualTo(expectedOutput)
    }
}