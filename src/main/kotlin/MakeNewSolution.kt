import graphMateKT.readInt
import graphMateKT.readString
import java.io.File
import java.nio.file.Files.createDirectories
import kotlin.io.path.Path

private fun createSolution(name: String) = """package graphMateKT.solutions

import fastInputReader.InputReader
import java.io.InputStream

internal fun main() {
    val ans = $name(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/$name */
internal fun $name(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val n = scanner.nextInt()
    repeat(n) {

    }
    return ""
}
"""

private fun createSolutionTest(name: String, nrOfSampleInputs: Int): String {
    var test = """import graphMateKT.solutions.$name
        
import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class ${name.capitalize()}Test {
"""
    repeat(nrOfSampleInputs) {
        test += """
    @Test
    fun ${name}${'a' + it}() {
        val expectedOutput = ""${'"'}${'"'}${'"'}${'"'}
        File("src/test/SampleInput/${name.capitalize()}/input${it + 1}").inputStream().use{
            assertThat($name(it)).isEqualTo(expectedOutput)
        }
    }
"""
    }
    test += """
    @Test
    fun ${name}Speed() {
        val expectedOutput = ""${'"'}${'"'}${'"'}${'"'}
        val input = mutableListOf("")
        val line = ""
        repeat(100_000){
            input.add(line)
        }
        input.joinToString("\n").byteInputStream().use {
            assertThat(${name}(it)).isEqualTo(expectedOutput)
        }
    }
}"""
    return test
}

private fun main() {
    print("Name of the programming puzzle: ")
    val name = readString()
    print("How many sample inputs? ")
    val nrOfSampleInputs = readInt()
    val example = createSolution(name)
    val exampleTest = createSolutionTest(name, nrOfSampleInputs)

    val exampleFile = File("src/main/kotlin/graphMateKT/solutions/${name.capitalize()}.kt")
    val exampleTestFile = File("src/test/kotlin/Test${name.capitalize()}.kt")
    val testInputDirectoryPath = "src/test/SampleInput/${name.capitalize()}"
    exampleFile.writeText(example)
    exampleTestFile.writeText(exampleTest)
    createDirectories(Path(testInputDirectoryPath))
    println(
        "Created solutions file: ${exampleFile.absolutePath}\n" +
                "test file: ${exampleTestFile.absolutePath}"
    )
    repeat(nrOfSampleInputs) {
        val testInputFile = File("${testInputDirectoryPath}/input${it + 1}")
        testInputFile.writeText("")
        println("test input file: ${testInputFile.absolutePath}")
    }
}
