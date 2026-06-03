import graphMateKT.solutions.dominos

import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.debug
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import java.io.File
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class DominosTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }

    @Test
    fun dominosa() {
        val expectedOutput = """1
2
"""
        _reader = File("src/test/SampleInput/Dominos/input1").inputStream().bufferedReader()
        assertThat(dominos()).isEqualTo(expectedOutput)
    }

    @Test
    fun dominosb() {
        val time = measureTimeMillis {
            _reader = File("src/test/SampleInput/Dominos/input2").inputStream().bufferedReader()
            dominos()
        }
        debug("dominos time use: $time ms")
    }

    @Test
    fun dominosc() {
        val expectedOutput = """3
"""
        _reader = File("src/test/SampleInput/Dominos/input3").inputStream().bufferedReader()
        assertThat(dominos()).isEqualTo(expectedOutput)
    }
}