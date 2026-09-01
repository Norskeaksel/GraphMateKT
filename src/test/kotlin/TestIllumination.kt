import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.debug
import graphMateKT.solutions.illumination
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import java.io.File
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class IlluminationTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }

    @Test
    fun illuminationa() {
        val expectedOutput = """1"""
        _reader = File("src/test/SampleInput/Illumination/input1").inputStream().bufferedReader()
        assertThat(illumination()).isEqualTo(expectedOutput)
    }


    @Test
    fun illuminationb() {
        val expectedOutput = """0"""
        _reader = File("src/test/SampleInput/Illumination/input2").inputStream().bufferedReader()
        assertThat(illumination()).isEqualTo(expectedOutput)
    }

    @Test
    fun AilluminationSpeedTest() {
        val expectedOutput = """1"""
        _reader = File("src/test/SampleInput/Illumination/input3").inputStream().bufferedReader()
        var ans: String
        val time = measureTimeMillis {
            ans = illumination()
        }
        debug("Illumination speed test time: $time ms")
        assertThat(ans).isEqualTo(expectedOutput)
    }

    @Test
    fun illuminationc() {
        val expectedOutput = """1"""
        _reader = File("src/test/SampleInput/Illumination/input4").inputStream().bufferedReader()
        assertThat(illumination()).isEqualTo(expectedOutput)
    }
}