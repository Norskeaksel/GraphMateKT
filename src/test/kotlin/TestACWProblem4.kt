import graphMateKT.solutions.ACWProblem4


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class AWCProblem4Test {

    @Test
    fun ACWProblem4a() {
        val expectedOutput = 7
        File("src/test/SampleInput/ACWProblem4/input2").inputStream().use {
            assertThat(ACWProblem4(it)).isEqualTo(expectedOutput)
        }
    }
}