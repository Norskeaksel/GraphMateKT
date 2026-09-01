import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.solutions.torn2pieces
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import java.io.File
import org.junit.jupiter.api.Test

class Torn2piecesTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }

    @Test
    fun torn2piecesa() {
        val expectedOutput = "Uptown Midtown Downtown"
        _reader = File("src/test/SampleInput/Torn2pieces/input1").inputStream().bufferedReader()
        assertThat(torn2pieces()).isEqualTo(expectedOutput)
    }

    @Test
    fun torn2piecesb() {
        val expectedOutput = "F E D B A"
        _reader = File("src/test/SampleInput/Torn2pieces/input2").inputStream().bufferedReader()
        assertThat(torn2pieces()).isEqualTo(expectedOutput)
    }

    @Test
    fun torn2piecesc() {
        val expectedOutput = "no route found"
        _reader = File("src/test/SampleInput/Torn2pieces/input3").inputStream().bufferedReader()
        assertThat(torn2pieces()).isEqualTo(expectedOutput)
    }
    @Test
    fun torn2piecesd() {
        val expectedOutput = "no route found"
        _reader = File("src/test/SampleInput/Torn2pieces/input4").inputStream().bufferedReader()
        assertThat(torn2pieces()).isEqualTo(expectedOutput)
    }
}
