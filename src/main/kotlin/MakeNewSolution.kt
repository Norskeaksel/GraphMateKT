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
import graphMateKT.debug
import kotlin.system.measureTimeMillis

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
        val input = listOf("1") + List(100) { "1".repeat(100) }
        val expectedOutput = ""${'"'}${'"'}${'"'}${'"'}
        makeStringsTestInput(input).use { input ->
            val time = measureTimeMillis {
                assertThat($name(input)).isEqualTo(expectedOutput)
            }
            debug("$name time use: ${'$'}time ms")
        }
    }
}"""
    return test
}
// TODO
//  1. add question: "Use fastInputReader? [Y/n]"
//  2. make it runnable from the command line
//  3. make it update generate-solution.sh
//  4. make users paste example input and answers

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
