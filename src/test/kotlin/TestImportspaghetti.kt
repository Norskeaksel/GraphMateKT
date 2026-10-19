import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.solutions.importspaghetti
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import java.io.File
import org.junit.jupiter.api.Test

class ImportspaghettiTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }   

    @Test
    fun importspaghettia() {
        val expectedOutput = """c"""
        _reader = File("src/test/SampleInput/Importspaghetti/input1").inputStream().bufferedReader()
        assertThat(importspaghetti()).isEqualTo(expectedOutput)
    }


    @Test
    fun importspaghettib() {
        val expectedOutput = """SHIP IT"""
        _reader = File("src/test/SampleInput/Importspaghetti/input2").inputStream().bufferedReader()
        assertThat(importspaghetti()).isEqualTo(expectedOutput)
    }


    @Test
    fun importspaghettic() {
        val expectedOutput = """classa classb execd"""
        _reader = File("src/test/SampleInput/Importspaghetti/input3").inputStream().bufferedReader()
        assertThat(importspaghetti()).isEqualTo(expectedOutput)
    }
}