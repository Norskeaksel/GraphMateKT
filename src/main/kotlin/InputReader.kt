/**
 * A fast input reader for any [InputStream] (especially `System.in`).
 *
 * This reader is optimized for competitive-programming style input and assumes
 * well-formed data (it does not perform validation).
 *
 * @author William Fiset, william.alexandre.fiset@gmail.com. Auto translated to Kotlin by IntelliJ.
 */

import java.io.IOException
import java.io.InputStream

class InputReader @JvmOverloads constructor(
    stream: InputStream = DEFAULT_STREAM,
    bufferSize: Int = DEFAULT_BUFFER_SIZE
) {
    // Variables associated with the byte buffer.
    private val buf: ByteArray
    private val bufferSize: Int
    private var bufIndex = 0
    private var numBytesRead = 0

    private val stream: InputStream

    // A reusable character buffer when reading string data.
    private var charBuffer: CharArray

    /**
     * Create an InputReader that reads from standard input.
     * @param bufferSize    The buffer size for this input reader.
     */
    constructor(bufferSize: Int) : this(DEFAULT_STREAM, bufferSize)

    init {
        require(!(stream == null || bufferSize <= 0))
        buf = ByteArray(bufferSize)
        charBuffer = CharArray(128)
        this.bufferSize = bufferSize
        this.stream = stream
    }

    /**
     * Reads a single character from the input stream.
     * @return Returns the byte value of the next character in the buffer and EOF
     * at the end of the stream.
     * @throws IOException throws exception if there is no more data to read
     */
    @Throws(IOException::class)
    private fun read(): Byte {
        if (numBytesRead == EOF.toInt()) throw IOException()

        if (bufIndex >= numBytesRead) {
            bufIndex = 0
            numBytesRead = stream.read(buf)
            if (numBytesRead == EOF.toInt()) return EOF
        }

        return buf[bufIndex++]
    }

    /**
     * Read values from the input stream until you reach a character with a
     * higher ASCII value than 'token'.
     * @param token The token is a value which we use to stop reading junk out of
     * the stream.
     * @return Returns 0 if a value greater than the token was reached or -1 if
     * the end of the stream was reached.
     * @throws IOException Throws exception at end of stream.
     */
    @Throws(IOException::class)
    private fun readJunk(token: Int): Int {
        if (numBytesRead == EOF.toInt()) return EOF.toInt()

        // Seek to the first valid position index
        do {
            while (bufIndex < numBytesRead) {
                if (buf[bufIndex] > token) return 0
                bufIndex++
            }

            // reload buffer
            numBytesRead = stream.read(buf)
            if (numBytesRead == EOF.toInt()) return EOF.toInt()
            bufIndex = 0
        } while (true)
    }

    /**
     * Reads a single byte from the input stream.
     * @return The next byte in the input stream
     * @throws IOException Throws exception at end of stream.
     */
    @Throws(IOException::class)
    fun nextByte(): Byte {
        return nextInt().toByte()
    }

    /**
     * Reads a 32 bit signed integer from input stream.
     * @return The next integer value in the stream.
     * @throws IOException Throws exception at end of stream.
     */
    @Throws(IOException::class)
    fun nextInt(): Int {
        if (readJunk(DASH - 1) == EOF.toInt()) throw IOException()
        var sgn = 1
        var res = 0

        val c = buf[bufIndex].toInt()
        if (c == DASH.toInt()) {
            sgn = -1
            bufIndex++
        }

        do {
            while (bufIndex < numBytesRead) {
                if (buf[bufIndex] > SPACE) {
                    res = (res shl 3) + (res shl 1)
                    res += ints[buf[bufIndex++].toInt()]
                } else {
                    bufIndex++
                    return res * sgn
                }
            }

            // Reload buffer
            numBytesRead = stream.read(buf)
            if (numBytesRead == EOF.toInt()) return res * sgn
            bufIndex = 0
        } while (true)
    }

    /**
     * Reads a 64 bit signed long from input stream.
     * @return The next long value in the stream.
     * @throws IOException Throws exception at end of stream.
     */
    @Throws(IOException::class)
    fun nextLong(): Long {
        if (readJunk(DASH - 1) == EOF.toInt()) throw IOException()
        var sgn = 1
        var res = 0L
        val c = buf[bufIndex].toInt()
        if (c == DASH.toInt()) {
            sgn = -1
            bufIndex++
        }

        do {
            while (bufIndex < numBytesRead) {
                if (buf[bufIndex] > SPACE) {
                    res = (res shl 3) + (res shl 1)
                    res += ints[buf[bufIndex++].toInt()].toLong()
                } else {
                    bufIndex++
                    return res * sgn
                }
            }

            // Reload buffer
            numBytesRead = stream.read(buf)
            if (numBytesRead == EOF.toInt()) return res * sgn
            bufIndex = 0
        } while (true)
    }

    /**
     * Doubles the size of the internal char buffer for strings
     */
    private fun doubleCharBufferSize() {
        charBuffer = charBuffer.copyOf(charBuffer.size shl 1)
    }

    /**
     * Reads a line from the input stream.
     * @return Returns a line from the input stream in the form a String not
     * including the new line character. Returns `null` when there are
     * no more lines.
     * @throws IOException Throws IOException when something terrible happens.
     */
    @Throws(IOException::class)
    fun nextLine(): String? {
        val c: Int
        try {
            c = read().toInt()
        } catch (e: IOException) {
            return null
        }
        if (c == NEW_LINE.toInt()) return "" // Empty line

        if (c == EOF.toInt()) return null // EOF


        var i = 0
        charBuffer[i++] = c.toChar()

        do {
            while (bufIndex < numBytesRead) {
                if (buf[bufIndex] != NEW_LINE) {
                    if (i == charBuffer.size) doubleCharBufferSize()
                    charBuffer[i++] = Char(buf[bufIndex++].toUShort())
                } else {
                    bufIndex++
                    return String(charBuffer, 0, i)
                }
            }

            // Reload buffer
            numBytesRead = stream.read(buf)
            if (numBytesRead == EOF.toInt()) return String(charBuffer, 0, i)
            bufIndex = 0
        } while (true)
    }

    /**
     * Reads the next token as a string.
     *
     * Token delimiters are ASCII characters `<= 32` (spaces, tabs, newlines, and EOF).
     *
     * @return The next token, or `null` if EOF is reached before a token starts.
     */
    @Throws(IOException::class)
    fun nextString(): String? {
        if (numBytesRead == EOF.toInt()) return null
        if (readJunk(SPACE.toInt()) == EOF.toInt()) return null

        var i = 0
        while (true) {
            while (bufIndex < numBytesRead) {
                if (buf[bufIndex] > SPACE) {
                    if (i == charBuffer.size) doubleCharBufferSize()
                    charBuffer[i++] = Char(buf[bufIndex++].toUShort())
                } else {
                    bufIndex++
                    return String(charBuffer, 0, i)
                }
            }

            // Reload buffer
            numBytesRead = stream.read(buf)
            if (numBytesRead == EOF.toInt()) return String(charBuffer, 0, i)
            bufIndex = 0
        }
    }

    /**
     * Reads the next token and parses it as a [Double].
     *
     * @return The parsed double value.
     */
    @Throws(IOException::class)
    fun nextDouble(): Double {
        val doubleVal = nextString()
        if (doubleVal == null) throw IOException()
        return doubleVal.toDouble()
    }

    /**
     * Reads the next token as a [Double] using a faster parsing path.
     *
     * This is typically faster than [nextDouble], but it may be slightly less accurate for
     * very long decimal fractions. It reads up to [MAX_DECIMAL_PRECISION] decimal digits.
     *
     * @return The parsed double value.
     */
    @Throws(IOException::class)
    fun nextDoubleFast(): Double {
        var c = read().toInt()
        var sgn = 1
        while (c <= SPACE) c = read().toInt() // while c is either: ' ', '\n', EOF

        if (c == DASH.toInt()) {
            sgn = -1
            c = read().toInt()
        }
        var res = 0.0
        // while c is not: ' ', '\n', '.' or -1
        while (c > DOT) {
            res *= 10.0
            res += ints[c].toDouble()
            c = read().toInt()
        }
        if (c == DOT.toInt()) {
            var i = 0
            c = read().toInt()
            // while c is digit and we are less than the maximum decimal precision
            while (c > SPACE && i < MAX_DECIMAL_PRECISION) {
                res += doubles[ints[c]]!![i++]
                c = read().toInt()
            }
        }
        return res * sgn
    }

    /** Reads an array of `n` byte values. */
    @Throws(IOException::class)
    fun nextByteArray(n: Int): ByteArray {
        val ar = ByteArray(n)
        for (i in 0..<n) ar[i] = nextByte()
        return ar
    }

    /** Reads an array of `n` integer values. */
    @Throws(IOException::class)
    fun nextIntArray(n: Int): IntArray {
        val ar = IntArray(n)
        for (i in 0..<n) ar[i] = nextInt()
        return ar
    }

    /** Reads an array of `n` long values. */
    @Throws(IOException::class)
    fun nextLongArray(n: Int): LongArray {
        val ar = LongArray(n)
        for (i in 0..<n) ar[i] = nextLong()
        return ar
    }

    /** Reads an array of `n` double values. */
    @Throws(IOException::class)
    fun nextDoubleArray(n: Int): DoubleArray {
        val ar = DoubleArray(n)
        for (i in 0..<n) ar[i] = nextDouble()
        return ar
    }

    /** Reads an array of `n` doubles using [nextDoubleFast]. */
    @Throws(IOException::class)
    fun nextDoubleArrayFast(n: Int): DoubleArray {
        val ar = DoubleArray(n)
        for (i in 0..<n) ar[i] = nextDoubleFast()
        return ar
    }

    /** Reads an array of `n` string tokens. */
    @Throws(IOException::class)
    fun nextStringArray(n: Int): Array<String?> {
        val ar = arrayOfNulls<String>(n)
        for (i in 0..<n) {
            val str = nextString()
            if (str == null) throw IOException()
            ar[i] = str
        }
        return ar
    }

    /** Reads a 1-based byte array of size `n + 1` (index `0` unused). */
    @Throws(IOException::class)
    fun nextByteArray1(n: Int): ByteArray {
        val ar = ByteArray(n + 1)
        for (i in 1..n) ar[i] = nextByte()
        return ar
    }

    /** Reads a 1-based integer array of size `n + 1` (index `0` unused). */
    @Throws(IOException::class)
    fun nextIntArray1(n: Int): IntArray {
        val ar = IntArray(n + 1)
        for (i in 1..n) ar[i] = nextInt()
        return ar
    }

    /** Reads a 1-based long array of size `n + 1` (index `0` unused). */
    @Throws(IOException::class)
    fun nextLongArray1(n: Int): LongArray {
        val ar = LongArray(n + 1)
        for (i in 1..n) ar[i] = nextLong()
        return ar
    }

    /** Reads a 1-based double array of size `n + 1` (index `0` unused). */
    @Throws(IOException::class)
    fun nextDoubleArray1(n: Int): DoubleArray {
        val ar = DoubleArray(n + 1)
        for (i in 1..n) ar[i] = nextDouble()
        return ar
    }

    /** Reads a 1-based double array of size `n + 1` using [nextDoubleFast]. */
    @Throws(IOException::class)
    fun nextDoubleArrayFast1(n: Int): DoubleArray {
        val ar = DoubleArray(n + 1)
        for (i in 1..n) ar[i] = nextDoubleFast()
        return ar
    }

    /** Reads a 1-based string array of size `n + 1` (index `0` unused). */
    @Throws(IOException::class)
    fun nextStringArray1(n: Int): Array<String?> {
        val ar = arrayOfNulls<String>(n + 1)
        for (i in 1..n) ar[i] = nextString()
        return ar
    }

    /** Reads a `rows x cols` matrix of bytes. */
    @Throws(IOException::class)
    fun nextByteMatrix(rows: Int, cols: Int): Array<ByteArray?> {
        val matrix = Array<ByteArray?>(rows) { ByteArray(cols) }
        for (i in 0..<rows) for (j in 0..<cols) matrix[i]!![j] = nextByte()
        return matrix
    }

    /** Reads a `rows x cols` matrix of integers. */
    @Throws(IOException::class)
    fun nextIntMatrix(rows: Int, cols: Int): Array<IntArray?> {
        val matrix = Array<IntArray?>(rows) { IntArray(cols) }
        for (i in 0..<rows) for (j in 0..<cols) matrix[i]!![j] = nextInt()
        return matrix
    }

    /** Reads a `rows x cols` matrix of longs. */
    @Throws(IOException::class)
    fun nextLongMatrix(rows: Int, cols: Int): Array<LongArray?> {
        val matrix = Array<LongArray?>(rows) { LongArray(cols) }
        for (i in 0..<rows) for (j in 0..<cols) matrix[i]!![j] = nextLong()
        return matrix
    }

    /** Reads a `rows x cols` matrix of doubles. */
    @Throws(IOException::class)
    fun nextDoubleMatrix(rows: Int, cols: Int): Array<DoubleArray?> {
        val matrix = Array<DoubleArray?>(rows) { DoubleArray(cols) }
        for (i in 0..<rows) for (j in 0..<cols) matrix[i]!![j] = nextDouble()
        return matrix
    }

    /** Reads a `rows x cols` matrix of doubles using [nextDoubleFast]. */
    @Throws(IOException::class)
    fun nextDoubleMatrixFast(rows: Int, cols: Int): Array<DoubleArray?> {
        val matrix = Array<DoubleArray?>(rows) { DoubleArray(cols) }
        for (i in 0..<rows) for (j in 0..<cols) matrix[i]!![j] = nextDoubleFast()
        return matrix
    }

    /** Reads a `rows x cols` matrix of string tokens. */
    @Throws(IOException::class)
    fun nextStringMatrix(rows: Int, cols: Int): Array<Array<String?>?> {
        val matrix = Array<Array<String?>?>(rows) { arrayOfNulls<String>(cols) }
        for (i in 0..<rows) for (j in 0..<cols) matrix[i]!![j] = nextString()
        return matrix
    }

    /** Reads a 1-based `(rows + 1) x (cols + 1)` matrix of bytes. */
    @Throws(IOException::class)
    fun nextByteMatrix1(rows: Int, cols: Int): Array<ByteArray?> {
        val matrix = Array<ByteArray?>(rows + 1) { ByteArray(cols + 1) }
        for (i in 1..rows) for (j in 1..cols) matrix[i]!![j] = nextByte()
        return matrix
    }

    /** Reads a 1-based `(rows + 1) x (cols + 1)` matrix of integers. */
    @Throws(IOException::class)
    fun nextIntMatrix1(rows: Int, cols: Int): Array<IntArray?> {
        val matrix = Array<IntArray?>(rows + 1) { IntArray(cols + 1) }
        for (i in 1..rows) for (j in 1..cols) matrix[i]!![j] = nextInt()
        return matrix
    }

    /** Reads a 1-based `(rows + 1) x (cols + 1)` matrix of longs. */
    @Throws(IOException::class)
    fun nextLongMatrix1(rows: Int, cols: Int): Array<LongArray?> {
        val matrix = Array<LongArray?>(rows + 1) { LongArray(cols + 1) }
        for (i in 1..rows) for (j in 1..cols) matrix[i]!![j] = nextLong()
        return matrix
    }

    /** Reads a 1-based `(rows + 1) x (cols + 1)` matrix of doubles. */
    @Throws(IOException::class)
    fun nextDoubleMatrix1(rows: Int, cols: Int): Array<DoubleArray?> {
        val matrix = Array<DoubleArray?>(rows + 1) { DoubleArray(cols + 1) }
        for (i in 1..rows) for (j in 1..cols) matrix[i]!![j] = nextDouble()
        return matrix
    }

    /** Reads a 1-based `(rows + 1) x (cols + 1)` matrix of doubles using [nextDoubleFast]. */
    @Throws(IOException::class)
    fun nextDoubleMatrixFast1(rows: Int, cols: Int): Array<DoubleArray?> {
        val matrix = Array<DoubleArray?>(rows + 1) { DoubleArray(cols + 1) }
        for (i in 1..rows) for (j in 1..cols) matrix[i]!![j] = nextDoubleFast()
        return matrix
    }

    /** Reads a 1-based `(rows + 1) x (cols + 1)` matrix of string tokens. */
    @Throws(IOException::class)
    fun nextStringMatrix1(rows: Int, cols: Int): Array<Array<String?>?> {
        val matrix = Array<Array<String?>?>(rows + 1) { arrayOfNulls<String>(cols + 1) }
        for (i in 1..rows) for (j in 1..cols) matrix[i]!![j] = nextString()
        return matrix
    }

    /** Closes the underlying input stream. */
    @Throws(IOException::class)
    fun close() {
        stream.close()
    }

    companion object {
        /**
         * The default size of the InputReader's buffer is 2<sup>16</sup>.
         */
        private val DEFAULT_BUFFER_SIZE = 1 shl 16

        /**
         * The default stream for the InputReader is standard input.
         */
        private val DEFAULT_STREAM: InputStream = System.`in`

        /**
         * The maximum number of accurate decimal digits the method [nextDoubleFast()][.nextDoubleFast] can read.
         * Currently this value is set to 21 because it is the maximum number of digits a double precision float can have at the moment.
         */
        private const val MAX_DECIMAL_PRECISION = 21

        /** End-of-file `EOF` byte marker. */
        private val EOF: Byte = -1

        /** Newline character `\n`. */
        private const val NEW_LINE: Byte = 10

        /** Space character ` `. */
        private const val SPACE: Byte = 32

        /** Dash character `-`. */
        private const val DASH: Byte = 45

        /** Dot character `.`. */
        private const val DOT: Byte = 46

        /** Lookup table mapping ASCII digits `'0'..'9'` to int values. */
        private val ints = IntArray(58)

        init {
            for (i in 48..57) ints[i] = i - 48
        }

        // @formatter:off
        /** Primitive lookup table used by [nextDoubleFast] to accumulate fractional values. */
        private val doubles = arrayOf<DoubleArray?>(
            doubleArrayOf( 0.0, 0.00, 0.000, 0.0000, 0.00000, 0.000000, 0.0000000, 0.00000000, 0.000000000, 0.0000000000, 0.00000000000, 0.000000000000, 0.0000000000000, 0.00000000000000, 0.000000000000000, 0.0000000000000000, 0.00000000000000000, 0.000000000000000000, 0.0000000000000000000, 0.00000000000000000000, 0.000000000000000000000 ),
            doubleArrayOf( 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 0.0000001, 0.00000001, 0.000000001, 0.0000000001, 0.00000000001, 0.000000000001, 0.0000000000001, 0.00000000000001, 0.000000000000001, 0.0000000000000001, 0.00000000000000001, 0.000000000000000001, 0.0000000000000000001, 0.00000000000000000001, 0.000000000000000000001 ),
            doubleArrayOf( 0.2, 0.02, 0.002, 0.0002, 0.00002, 0.000002, 0.0000002, 0.00000002, 0.000000002, 0.0000000002, 0.00000000002, 0.000000000002, 0.0000000000002, 0.00000000000002, 0.000000000000002, 0.0000000000000002, 0.00000000000000002, 0.000000000000000002, 0.0000000000000000002, 0.00000000000000000002, 0.000000000000000000002 ),
            doubleArrayOf( 0.3, 0.03, 0.003, 0.0003, 0.00003, 0.000003, 0.0000003, 0.00000003, 0.000000003, 0.0000000003, 0.00000000003, 0.000000000003, 0.0000000000003, 0.00000000000003, 0.000000000000003, 0.0000000000000003, 0.00000000000000003, 0.000000000000000003, 0.0000000000000000003, 0.00000000000000000003, 0.000000000000000000003 ),
            doubleArrayOf( 0.4, 0.04, 0.004, 0.0004, 0.00004, 0.000004, 0.0000004, 0.00000004, 0.000000004, 0.0000000004, 0.00000000004, 0.000000000004, 0.0000000000004, 0.00000000000004, 0.000000000000004, 0.0000000000000004, 0.00000000000000004, 0.000000000000000004, 0.0000000000000000004, 0.00000000000000000004, 0.000000000000000000004 ),
            doubleArrayOf( 0.5, 0.05, 0.005, 0.0005, 0.00005, 0.000005, 0.0000005, 0.00000005, 0.000000005, 0.0000000005, 0.00000000005, 0.000000000005, 0.0000000000005, 0.00000000000005, 0.000000000000005, 0.0000000000000005, 0.00000000000000005, 0.000000000000000005, 0.0000000000000000005, 0.00000000000000000005, 0.000000000000000000005 ),
            doubleArrayOf( 0.6, 0.06, 0.006, 0.0006, 0.00006, 0.000006, 0.0000006, 0.00000006, 0.000000006, 0.0000000006, 0.00000000006, 0.000000000006, 0.0000000000006, 0.00000000000006, 0.000000000000006, 0.0000000000000006, 0.00000000000000006, 0.000000000000000006, 0.0000000000000000006, 0.00000000000000000006, 0.000000000000000000006 ),
            doubleArrayOf( 0.7, 0.07, 0.007, 0.0007, 0.00007, 0.000007, 0.0000007, 0.00000007, 0.000000007, 0.0000000007, 0.00000000007, 0.000000000007, 0.0000000000007, 0.00000000000007, 0.000000000000007, 0.0000000000000007, 0.00000000000000007, 0.000000000000000007, 0.0000000000000000007, 0.00000000000000000007, 0.000000000000000000007 ),
            doubleArrayOf( 0.8, 0.08, 0.008, 0.0008, 0.00008, 0.000008, 0.0000008, 0.00000008, 0.000000008, 0.0000000008, 0.00000000008, 0.000000000008, 0.0000000000008, 0.00000000000008, 0.000000000000008, 0.0000000000000008, 0.00000000000000008, 0.000000000000000008, 0.0000000000000000008, 0.00000000000000000008, 0.000000000000000000008 ),
            doubleArrayOf( 0.9, 0.09, 0.009, 0.0009, 0.00009, 0.000009, 0.0000009, 0.00000009, 0.000000009, 0.0000000009, 0.00000000009, 0.000000000009, 0.0000000000009, 0.00000000000009, 0.000000000000009, 0.0000000000000009, 0.00000000000000009, 0.000000000000000009, 0.0000000000000000009, 0.00000000000000000009, 0.000000000000000000009 )
        )
    }
}