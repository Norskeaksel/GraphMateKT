import graphMateKT.solutions.robotTurtles
        

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class RobotTurtlesTest {

    @Test
    fun robotTurtlesa() {
        val expectedOutput = """FLFRXFLFRFLFRF"""
        File("src/test/SampleInput/RobotTurtles/input1").inputStream().use{
            assertThat(robotTurtles(it).length).isEqualTo(expectedOutput.length)
        }
    }

    @Test
    fun robotTurtlesb() {
        val expectedOutput = """FLFRXFLXFRFLXFRF"""
        File("src/test/SampleInput/RobotTurtles/input2").inputStream().use{
            assertThat(robotTurtles(it).length).isEqualTo(expectedOutput.length)
        }
    }

    @Test
    fun robotTurtlesc() {
        val expectedOutput = """FLFRXFLFRFXFFFLFFLF"""
        File("src/test/SampleInput/RobotTurtles/input3").inputStream().use{
            assertThat(robotTurtles(it).length).isEqualTo(expectedOutput.length)
        }
    }

    @Test
    fun robotTurtlesd() {
        val expectedOutput = """no solution"""
        File("src/test/SampleInput/RobotTurtles/input4").inputStream().use{
            assertThat(robotTurtles(it)).isEqualTo(expectedOutput)
        }
    }
}