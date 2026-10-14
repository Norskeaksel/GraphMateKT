import graphMateKT.solutions.ACWProblem2
        

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class ACWProblem2Test {

    @Test
    fun ACWProblem2a() {
        val expectedOutput = 4
        File("src/test/SampleInput/ACWProblem2/input1").inputStream().use{
            assertThat(ACWProblem2(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun ACWProblem2b() {
        val expectedOutput = 48
        File("src/test/SampleInput/ACWProblem2/input2").inputStream().use{
            assertThat(ACWProblem2(it)).isEqualTo(expectedOutput)
        }
    }
}