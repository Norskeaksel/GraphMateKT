import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.solutions.brexit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import java.io.File
import org.junit.jupiter.api.Test

class BrexitTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }   

    @Test
    fun brexita() {
        val expectedOutput = """stay"""
        _reader = File("src/test/SampleInput/Brexit/input1").inputStream().bufferedReader()
        assertThat(brexit()).isEqualTo(expectedOutput)
    }


    @Test
    fun brexitb() {
        val expectedOutput = """leave"""
        _reader = File("src/test/SampleInput/Brexit/input2").inputStream().bufferedReader()
        assertThat(brexit()).isEqualTo(expectedOutput)
    }


    @Test
    fun brexitc() {
        val expectedOutput = """stay"""
        _reader = File("src/test/SampleInput/Brexit/input3").inputStream().bufferedReader()
        assertThat(brexit()).isEqualTo(expectedOutput)
    }


    @Test
    fun brexitd() {
        val expectedOutput = """leave"""
        _reader = File("src/test/SampleInput/Brexit/input4").inputStream().bufferedReader()
        assertThat(brexit()).isEqualTo(expectedOutput)
    }

}