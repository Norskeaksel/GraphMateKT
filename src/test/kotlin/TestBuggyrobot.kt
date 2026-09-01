import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.solutions.buggyrobot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import java.io.File
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class BuggyrobotTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }   

    @Test
    fun buggyrobota() {
        val expectedOutput = """1"""
        _reader = File("src/test/SampleInput/Buggyrobot/input1").inputStream().bufferedReader()
        assertThat(buggyrobot()).isEqualTo(expectedOutput)
    }


    @Test
    fun buggyrobotb() {
        val expectedOutput = """1"""
        _reader = File("src/test/SampleInput/Buggyrobot/input2").inputStream().bufferedReader()
        assertThat(buggyrobot()).isEqualTo(expectedOutput)
    }


    @Test
    fun buggyrobotc() {
        val expectedOutput = """2"""
        _reader = File("src/test/SampleInput/Buggyrobot/input3").inputStream().bufferedReader()
        assertThat(buggyrobot()).isEqualTo(expectedOutput)
    }


    @Test
    fun buggyrobotd() {
        val expectedOutput = """0"""
        _reader = File("src/test/SampleInput/Buggyrobot/input4").inputStream().bufferedReader()
        assertThat(buggyrobot()).isEqualTo(expectedOutput)
    }


    @Test
    fun buggyrobote() {
        val expectedOutput = """1"""
        _reader = File("src/test/SampleInput/Buggyrobot/input5").inputStream().bufferedReader()
        assertThat(buggyrobot()).isEqualTo(expectedOutput)
    }


    @Test
    fun buggyrobotf() {
        val expectedOutput = """0"""
        _reader = File("src/test/SampleInput/Buggyrobot/input6").inputStream().bufferedReader()
        assertThat(buggyrobot()).isEqualTo(expectedOutput)
    }
    @Test
    fun buggyrobotfSpeedTest() {
        val expectedOutput = """49"""
        _reader = File("src/test/SampleInput/Buggyrobot/input7").inputStream().bufferedReader()
        val ans:String
        val time = measureTimeMillis {
            ans = buggyrobot()
        }
        System.err.println("Buggy robot speedTime = $time ms")
        assertThat(ans).isEqualTo(expectedOutput)
    }

    @Test
    fun buggyrobotgSpeedTest2() {
        _reader = File("src/test/SampleInput/Buggyrobot/input8").inputStream().bufferedReader()
        val time = measureTimeMillis {
            buggyrobot()
        }
        System.err.println("Buggy robot speedTime = $time ms")
    }
    @Test
    fun buggyroboth() {
        val expectedOutput = """0"""
        _reader = File("src/test/SampleInput/Buggyrobot/input9").inputStream().bufferedReader()
        assertThat(buggyrobot()).isEqualTo(expectedOutput)
    }
}
