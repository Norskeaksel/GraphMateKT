import graphMateKT.solutions.hopscotch50
        
import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class Hopscotch50Test {

    @Test
    fun hopscotch50a() {
        val expectedOutput = 5
        File("src/test/SampleInput/Hopscotch50/input1").inputStream().use{
            assertThat(hopscotch50(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun hopscotch50b() {
        val expectedOutput = -1
        File("src/test/SampleInput/Hopscotch50/input2").inputStream().use{
            assertThat(hopscotch50(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun hopscotch50c() {
        val expectedOutput = -1
        File("src/test/SampleInput/Hopscotch50/input3").inputStream().use{
            assertThat(hopscotch50(it)).isEqualTo(expectedOutput)
        }
    }
}