// Solves https://open.kattis.com/problems/crosscountry?tab=metadata
import graphMateKT._reader
import graphMateKT.solutions.crossCountry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File


class CrossCountryTest {
    @Test
    fun testCrossCountry() {
        val expectedOutput = 11
        _reader = File("src/test/SampleInput/CrossCountry/input1").inputStream().bufferedReader()
        assertThat(crossCountry()).isEqualTo(expectedOutput)
    }
}