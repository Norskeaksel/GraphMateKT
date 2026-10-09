import graphMateKT.solutions.elevatortrouble
        

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class ElevatortroubleTest {

    @Test
    fun elevatortroublea() {
        val expectedOutput = """6"""
        File("src/test/SampleInput/Elevatortrouble/input1").inputStream().use{
            assertThat(elevatortrouble(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun elevatortroubleb() {
        val expectedOutput = """use the stairs"""
        File("src/test/SampleInput/Elevatortrouble/input2").inputStream().use{
            assertThat(elevatortrouble(it)).isEqualTo(expectedOutput)
        }
    }
}