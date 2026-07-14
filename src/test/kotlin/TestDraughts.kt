import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.solutions.draughts

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import java.io.File
import org.junit.jupiter.api.Test

class DraughtsTest {

    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }

    @Test
    fun draughtsa() {
        val expectedOutput = """2
4"""
        _reader = File("src/test/SampleInput/Draughts/input1").inputStream().bufferedReader()
        assertThat(draughts()).isEqualTo(expectedOutput)
    }
    @Test
    fun draughtsb() {
        val expectedOutput = """1
0"""
        _reader = File("src/test/SampleInput/Draughts/input2").inputStream().bufferedReader()
        assertThat(draughts()).isEqualTo(expectedOutput)
    }
    @Test
    fun draughtsc() {
        val expectedOutput = """0
1"""
        _reader = File("src/test/SampleInput/Draughts/input3").inputStream().bufferedReader()
        assertThat(draughts()).isEqualTo(expectedOutput)
    }
}