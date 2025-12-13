import graphMateKT.solutions.favourable
        
import graphMateKT.INPUT
import graphMateKT._reader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import java.io.File
import org.junit.jupiter.api.Test

class FavourableTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }   

    @Test
    fun favourablea() {
        val expectedOutput = """2
5
"""
        _reader = File("src/test/SampleInput/Favourable/input1").inputStream().bufferedReader()
        assertThat(favourable()).isEqualTo(expectedOutput)
    }

}