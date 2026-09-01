import graphMateKT.solutions.graduation
        
import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class GraduationTest {

    @Test
    fun Graduationa() {
        val expectedOutput = 1
        File("src/test/SampleInput/Graduation/input1").inputStream().use{
            assertThat(graduation(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun Graduationb() {
        val expectedOutput = 2
        File("src/test/SampleInput/Graduation/input2").inputStream().use{
            assertThat(graduation(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun Graduationc() {
        val expectedOutput = 3
        File("src/test/SampleInput/Graduation/input3").inputStream().use{
            assertThat(graduation(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun Graduationd() {
        val expectedOutput = 2
        File("src/test/SampleInput/Graduation/input4").inputStream().use{
            assertThat(graduation(it)).isEqualTo(expectedOutput)
        }
    }
    @Test
    fun GraduationSpeed() {
        val expectedOutput = 1
        File("src/test/SampleInput/Graduation/input5").inputStream().use{
            assertThat(graduation(it)).isEqualTo(expectedOutput)
        }
    }
}