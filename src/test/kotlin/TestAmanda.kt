import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.solutions.amanda
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import java.io.File

class AmandaTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }

    @Test
    fun amandaa() {
        val expectedOutput = """3"""
        _reader = File("src/test/SampleInput/Amanda/input1").inputStream().bufferedReader()
        assertThat(amanda()).isEqualTo(expectedOutput)
    }


    @Test
    fun amandab() {
        val expectedOutput = """impossible"""
        _reader = File("src/test/SampleInput/Amanda/input2").inputStream().bufferedReader()
        assertThat(amanda()).isEqualTo(expectedOutput)
    }


    @Test
    fun amandac() {
        val expectedOutput = """2"""
        _reader = File("src/test/SampleInput/Amanda/input3").inputStream().bufferedReader()
        assertThat(amanda()).isEqualTo(expectedOutput)
    }

    @Test
    fun amandad() {
        val expectedOutput = """3"""
        _reader = File("src/test/SampleInput/Amanda/input4").inputStream().bufferedReader()
        assertThat(amanda()).isEqualTo(expectedOutput)
    }
    @Test
    fun amandae() {
        val expectedOutput = """impossible"""
        _reader = File("src/test/SampleInput/Amanda/input5").inputStream().bufferedReader()
        assertThat(amanda()).isEqualTo(expectedOutput)
    }

    @Test
    fun amandaf() {
        val expectedOutput = """1"""
        _reader = File("src/test/SampleInput/Amanda/input6").inputStream().bufferedReader()
        assertThat(amanda()).isEqualTo(expectedOutput)

    }
}