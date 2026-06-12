import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.solutions.walkforest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import java.io.File
import org.junit.jupiter.api.Test

class WalkforestTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }   

    @Test
    fun walkforesta() {
        val expectedOutput = """2
4
"""
        _reader = File("src/test/SampleInput/Walkforest/input1").inputStream().bufferedReader()
        assertThat(walkforest()).isEqualTo(expectedOutput)
    }

    @Test
    fun walkforestb() {
        val expectedOutput = """4
"""
        _reader = File("src/test/SampleInput/Walkforest/input2").inputStream().bufferedReader()
        assertThat(walkforest()).isEqualTo(expectedOutput)
    }
    @Test
    fun walkforestc() {
        val expectedOutput = """1
"""
        _reader = File("src/test/SampleInput/Walkforest/input3").inputStream().bufferedReader()
        assertThat(walkforest()).isEqualTo(expectedOutput)
    }
}
