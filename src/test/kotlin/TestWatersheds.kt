import graphMateKT.solutions.watersheds
import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.debug
import org.junit.jupiter.api.AfterAll
import java.io.File
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class WatershedsTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }

    /*@Test
    fun watershedsa() {
        val expectedOutput = """Case #1:
a b b 
a a b 
a a a 
Case #2:
a a a a a a a a a b 
Case #3:
a a a 
b b b 
Case #4:
a a a a a 
a a b b a 
a b b b a 
a b b b a 
a a a a a 
Case #5:
a b c d e f g h i j k l m 
n o p q r s t u v w x y z 
Case #6:
a a a 
b a a 
b a c"""
        _reader = File("src/test/SampleInput/Watersheds/input1").inputStream().bufferedReader()
        assertThat(watersheds()).isEqualTo(expectedOutput)  TODO: fix solution now that bidirectional is not an option
    }*/

    @Test
    fun watershedSpeedTest() {
        _reader = File("src/test/SampleInput/Watersheds/input2").inputStream().bufferedReader()
        val time = measureTimeMillis {
            watersheds()
        }
        debug("Watersheds took $time ms")
    }
}