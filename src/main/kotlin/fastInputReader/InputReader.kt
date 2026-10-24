package fastInputReader

import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import kotlin.math.max

/**
 * A very fast input reader to read data from any InputStream (System.in in particular).
 * I primarily use this input reader for competitive programming to get an edge on the
 * time required to read input. That being said, this input reader assumes you know what
 * data types you are reading in and will not do any validation of input whatsoever!
 *
 * @author William Fiset, william.alexandre.fiset@gmail.com
 */
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

    // Reusable scratch space for tokens which straddle a buffer refill.
    private var tok = ByteArray(64)
    private var tokLen = 0

    /**
     * Create an InputReader that reads from standard input.
     * @param bufferSize    The buffer size for this input reader.
     */
    constructor(bufferSize: Int) : this(DEFAULT_STREAM, bufferSize)

    /**
     * Create an InputReader that reads from the given stream.
     * @param  stream        The [InputStream][InputStream] to read from.
     * @param  bufferSize    The size of the buffer to use.
     */
    /**
     * Create an InputReader that reads from standard input.
     */
    /**
     * Create an InputReader that reads from the given stream.
     * @param stream  The InputStream to read from.
     */
    init {
        require(!(stream == null || bufferSize <= 0))
        buf = ByteArray(bufferSize)
        this.bufferSize = bufferSize
        this.stream = stream
    }

    /**
     * Refills the buffer from the stream and resets bufIndex. Once the end of
     * the stream is reached the stream is never read from again.
     * @return The number of bytes read, or EOF.
     * @throws IOException If the underlying stream throws.
     */
    @Throws(IOException::class)
    private fun fill(): Int {
        bufIndex = 0
        return (if (numBytesRead == EOF) EOF else stream.read(buf, 0, bufferSize)).also { numBytesRead = it }
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
        if (numBytesRead == EOF) return EOF

        // Seek to the first valid position index
        do {
            while (bufIndex < numBytesRead) {
                if (buf[bufIndex] > token) return 0
                bufIndex++
            }

            // reload buffer
            numBytesRead = stream.read(buf, 0, bufferSize)
            if (numBytesRead == EOF) return EOF
            bufIndex = 0
        } while (true)
    }

    /**
     * Copies the partial token buf[start, numBytesRead) into the scratch buffer and keeps reading
     * until the token's delimiter (consumed) or EOF. The token ends up in tok[0, tokLen).
     * @param start  Index in buf where the token begins.
     * @param line   If true the delimiter is a new line, otherwise any byte <= ' '.
     */
    @Throws(IOException::class)
    private fun spill(start: Int, line: Boolean) {
        val buf = this.buf
        var t = tok
        var len = numBytesRead - start
        if (len > t.size) t = t.copyOf(max(len, t.size shl 1))
        System.arraycopy(buf, start, t, 0, len)
        var n: Int
        while ((fill().also { n = it }) != EOF) {
            var i = 0
            if (line) while (i < n && buf[i] != NEW_LINE) i++
            else while (i < n && buf[i] > SPACE) i++
            if (len + i > t.size) t = t.copyOf(max(len + i, t.size shl 1))
            System.arraycopy(buf, 0, t, len, i)
            len += i
            if (i < n) {
                bufIndex = i + 1
                break
            }
        }
        tok = t
        tokLen = len
    }

    /**
     * Reads a signed 8 bit integer written as text (e.g. "-12") from the input stream.
     * Values outside the byte range overflow silently.
     * @return The next byte value in the stream.
     * @throws IOException Throws exception at end of stream.
     */
    @Throws(IOException::class)
    fun nextByte(): Byte {
        return nextInt().toByte()
    }

    // Note: nextInt and nextLong deliberately keep the field based loop shape. Hoisting
    // bufIndex/numBytesRead into locals or using a sentinel terminated loop benchmarked
    // 15-50% slower on HotSpot C2, as did a SWAR (8 digits per long) parser.
    /**
     * Reads a 32 bit signed integer from input stream.
     * @return The next integer value in the stream.
     * @throws IOException Throws exception at end of stream.
     */
    @Throws(IOException::class)
    fun nextInt(): Int {
        if (readJunk(DASH - 1) == EOF) throw IOException()
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
                    res = res * 10 + buf[bufIndex++] - '0'.code
                } else {
                    bufIndex++
                    return res * sgn
                }
            }

            // Reload buffer
            numBytesRead = stream.read(buf, 0, bufferSize)
            if (numBytesRead == EOF) return res * sgn
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
        if (readJunk(DASH - 1) == EOF) throw IOException()
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
                    res = res * 10 + buf[bufIndex++] - '0'.code.toLong()
                } else {
                    bufIndex++
                    return res * sgn
                }
            }

            // Reload buffer
            numBytesRead = stream.read(buf, 0, bufferSize)
            if (numBytesRead == EOF) return res * sgn
            bufIndex = 0
        } while (true)
    }

    /**
     * Reads a line from the input stream.
     * @return Returns a line from the input stream in the form a String not
     * including the new line character ('\n'). A '\r' before it is kept. Returns `null` when there are
     * no more lines.
     * @throws IOException Throws IOException when something terrible happens.
     */
    @Throws(IOException::class)
    fun nextLine(): String? {
        if (bufIndex >= numBytesRead && fill() == EOF) return null

        val buf = this.buf
        val n = numBytesRead
        val start = bufIndex
        var i = start
        while (i < n && buf[i] != NEW_LINE) i++
        if (i < n) {
            bufIndex = i + 1
            return String(buf, start, i - start, LATIN1)
        }

        spill(start, true)
        return String(tok, 0, tokLen, LATIN1)
    }

    /**
     * Reads a string of characters from the input stream.
     * The delimiter separating a string of characters is set to be:
     * any ASCII value <= 32 meaning any spaces, new lines, tabs... or any non-ASCII byte
     * (bytes are compared as signed values). Returns null at the end of the stream.
     */
    @Throws(IOException::class)
    fun nextString(): String? {
        if (readJunk(SPACE.toInt()) == EOF) return null

        val buf = this.buf
        val n = numBytesRead
        val start = bufIndex
        var i = start
        while (i < n && buf[i] > SPACE) i++
        if (i < n) {
            bufIndex = i + 1
            return String(buf, start, i - start, LATIN1)
        }

        spill(start, false)
        return String(tok, 0, tokLen, LATIN1)
    }

    /** Reads a double from the input stream. The result is always identical to Double.parseDouble. */
    @Throws(IOException::class)
    fun nextDouble(): Double {
        return nextDouble(true)
    }

    /**
     * Reads a double value from the input stream, trading up to 1 ulp of accuracy for speed on
     * high precision input. For values with at most ~15 significant digits (most input) this is
     * identical to nextDouble(): same exact result, same speed. For 16+ significant digits, such as
     * the full precision output of Double.toString, nextDouble() falls back to Double.parseDouble
     * to stay exact, while this method keeps the fast path and may be off by 1 ulp (~2x faster).
     */
    @Throws(IOException::class)
    fun nextDoubleFast(): Double {
        return nextDouble(false)
    }

    @Throws(IOException::class)
    private fun nextDouble(exact: Boolean): Double {
        if (readJunk(SPACE.toInt()) == EOF) throw IOException()

        val buf = this.buf
        val n = numBytesRead
        val start = bufIndex
        var i = start
        while (i < n && buf[i] > SPACE) i++
        if (i < n) {
            bufIndex = i + 1
            return parseDouble(buf, start, i, exact)
        }

        spill(start, false)
        return parseDouble(tok, 0, tokLen, exact)
    }

    /** Read an array of n byte values */
    @Throws(IOException::class)
    fun nextByteArray(n: Int): ByteArray {
        val ar = ByteArray(n)
        for (i in 0..<n) ar[i] = nextByte()
        return ar
    }

    /** Read an integer array of size n */
    @Throws(IOException::class)
    fun nextIntArray(n: Int): IntArray {
        val ar = IntArray(n)
        for (i in 0..<n) ar[i] = nextInt()
        return ar
    }

    /** Read a long array of size n */
    @Throws(IOException::class)
    fun nextLongArray(n: Int): LongArray {
        val ar = LongArray(n)
        for (i in 0..<n) ar[i] = nextLong()
        return ar
    }

    /** Read a double array of size n */
    @Throws(IOException::class)
    fun nextDoubleArray(n: Int): DoubleArray {
        val ar = DoubleArray(n)
        for (i in 0..<n) ar[i] = nextDouble()
        return ar
    }

    /** Quickly read an array of doubles */
    @Throws(IOException::class)
    fun nextDoubleArrayFast(n: Int): DoubleArray {
        val ar = DoubleArray(n)
        for (i in 0..<n) ar[i] = nextDoubleFast()
        return ar
    }

    /** Read a string array of size n */
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

    /** Read a 1-based byte array of size n+1 */
    @Throws(IOException::class)
    fun nextByteArray1(n: Int): ByteArray {
        val ar = ByteArray(n + 1)
        for (i in 1..n) ar[i] = nextByte()
        return ar
    }

    /** Read a 1-based integer array of size n+1 */
    @Throws(IOException::class)
    fun nextIntArray1(n: Int): IntArray {
        val ar = IntArray(n + 1)
        for (i in 1..n) ar[i] = nextInt()
        return ar
    }

    /** Read a 1-based long array of size n+1 */
    @Throws(IOException::class)
    fun nextLongArray1(n: Int): LongArray {
        val ar = LongArray(n + 1)
        for (i in 1..n) ar[i] = nextLong()
        return ar
    }

    /** Read a 1-based double array of size n+1 */
    @Throws(IOException::class)
    fun nextDoubleArray1(n: Int): DoubleArray {
        val ar = DoubleArray(n + 1)
        for (i in 1..n) ar[i] = nextDouble()
        return ar
    }

    /** Quickly read a 1-based double array of size n+1 */
    @Throws(IOException::class)
    fun nextDoubleArrayFast1(n: Int): DoubleArray {
        val ar = DoubleArray(n + 1)
        for (i in 1..n) ar[i] = nextDoubleFast()
        return ar
    }

    /** Read a 1-based string array of size n+1 */
    @Throws(IOException::class)
    fun nextStringArray1(n: Int): Array<String?> {
        val ar = arrayOfNulls<String>(n + 1)
        for (i in 1..n) ar[i] = nextString()
        return ar
    }

    /** Read a two dimensional matrix of bytes of size rows x cols */
    @Throws(IOException::class)
    fun nextByteMatrix(rows: Int, cols: Int): Array<ByteArray?> {
        val matrix = Array<ByteArray?>(rows) { ByteArray(cols) }
        for (i in 0..<rows) for (j in 0..<cols) matrix[i]!![j] = nextByte()
        return matrix
    }

    /** Read a two dimensional matrix of ints of size rows x cols */
    @Throws(IOException::class)
    fun nextIntMatrix(rows: Int, cols: Int): Array<IntArray?> {
        val matrix = Array<IntArray?>(rows) { IntArray(cols) }
        for (i in 0..<rows) for (j in 0..<cols) matrix[i]!![j] = nextInt()
        return matrix
    }

    /** Read a two dimensional matrix of longs of size rows x cols */
    @Throws(IOException::class)
    fun nextLongMatrix(rows: Int, cols: Int): Array<LongArray?> {
        val matrix = Array<LongArray?>(rows) { LongArray(cols) }
        for (i in 0..<rows) for (j in 0..<cols) matrix[i]!![j] = nextLong()
        return matrix
    }

    /** Read a two dimensional matrix of doubles of size rows x cols */
    @Throws(IOException::class)
    fun nextDoubleMatrix(rows: Int, cols: Int): Array<DoubleArray?> {
        val matrix = Array<DoubleArray?>(rows) { DoubleArray(cols) }
        for (i in 0..<rows) for (j in 0..<cols) matrix[i]!![j] = nextDouble()
        return matrix
    }

    /** Quickly read a two dimensional matrix of doubles of size rows x cols */
    @Throws(IOException::class)
    fun nextDoubleMatrixFast(rows: Int, cols: Int): Array<DoubleArray?> {
        val matrix = Array<DoubleArray?>(rows) { DoubleArray(cols) }
        for (i in 0..<rows) for (j in 0..<cols) matrix[i]!![j] = nextDoubleFast()
        return matrix
    }

    /** Read a two dimensional matrix of Strings of size rows x cols */
    @Throws(IOException::class)
    fun nextStringMatrix(rows: Int, cols: Int): Array<Array<String?>?> {
        val matrix = Array<Array<String?>?>(rows) { arrayOfNulls<String>(cols) }
        for (i in 0..<rows) for (j in 0..<cols) matrix[i]!![j] = nextString()
        return matrix
    }

    /** Read a 1-based two dimensional matrix of bytes of size rows x cols */
    @Throws(IOException::class)
    fun nextByteMatrix1(rows: Int, cols: Int): Array<ByteArray?> {
        val matrix = Array<ByteArray?>(rows + 1) { ByteArray(cols + 1) }
        for (i in 1..rows) for (j in 1..cols) matrix[i]!![j] = nextByte()
        return matrix
    }

    /** Read a 1-based two dimensional matrix of ints of size rows x cols */
    @Throws(IOException::class)
    fun nextIntMatrix1(rows: Int, cols: Int): Array<IntArray?> {
        val matrix = Array<IntArray?>(rows + 1) { IntArray(cols + 1) }
        for (i in 1..rows) for (j in 1..cols) matrix[i]!![j] = nextInt()
        return matrix
    }

    /** Read a 1-based two dimensional matrix of longs of size rows x cols */
    @Throws(IOException::class)
    fun nextLongMatrix1(rows: Int, cols: Int): Array<LongArray?> {
        val matrix = Array<LongArray?>(rows + 1) { LongArray(cols + 1) }
        for (i in 1..rows) for (j in 1..cols) matrix[i]!![j] = nextLong()
        return matrix
    }

    /** Read a 1-based two dimensional matrix of doubles of size rows x cols */
    @Throws(IOException::class)
    fun nextDoubleMatrix1(rows: Int, cols: Int): Array<DoubleArray?> {
        val matrix = Array<DoubleArray?>(rows + 1) { DoubleArray(cols + 1) }
        for (i in 1..rows) for (j in 1..cols) matrix[i]!![j] = nextDouble()
        return matrix
    }

    /** Quickly read a 1-based two dimensional matrix of doubles of size rows x cols */
    @Throws(IOException::class)
    fun nextDoubleMatrixFast1(rows: Int, cols: Int): Array<DoubleArray?> {
        val matrix = Array<DoubleArray?>(rows + 1) { DoubleArray(cols + 1) }
        for (i in 1..rows) for (j in 1..cols) matrix[i]!![j] = nextDoubleFast()
        return matrix
    }

    /** Read a 1-based two dimensional matrix of Strings of size rows x cols */
    @Throws(IOException::class)
    fun nextStringMatrix1(rows: Int, cols: Int): Array<Array<String?>?> {
        val matrix = Array<Array<String?>?>(rows + 1) { arrayOfNulls<String>(cols + 1) }
        for (i in 1..rows) for (j in 1..cols) matrix[i]!![j] = nextString()
        return matrix
    }

    /** Closes the input stream */
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

        // Mantissas are accumulated into a long while below this bound, so m*10+9 never overflows.
        private const val MANTISSA_LIMIT = 100000000000000000L

        // Bytes map 1:1 to chars, and the JDK builds compact strings from it with a plain array copy.
        private val LATIN1: Charset = StandardCharsets.ISO_8859_1

        // End Of File (EOF) character
        private val EOF = -1

        // New line character: '\n'
        private const val NEW_LINE: Byte = 10

        // Space character: ' '
        private const val SPACE: Byte = 32

        // Dash character: '-'
        private const val DASH: Byte = 45

        // Exactly representable powers of ten, used by the fast path of the double parser.
        private val POW10 = doubleArrayOf(
            1e0, 1e1, 1e2, 1e3, 1e4, 1e5, 1e6, 1e7, 1e8, 1e9, 1e10, 1e11,
            1e12, 1e13, 1e14, 1e15, 1e16, 1e17, 1e18, 1e19, 1e20, 1e21, 1e22
        )

        /**
         * Parses the double in a[p, end). Values whose decimal mantissa fits in 53 bits with a
         * power of ten exponent of at most 22 are computed with a single correctly rounded multiply
         * or divide (Clinger's fast path); anything else is handed to Double.parseDouble.
         * @param exact  If false, excess mantissa digits are truncated rather than falling back.
         */
        private fun parseDouble(a: ByteArray, p: Int, end: Int, exact: Boolean): Double {
            var p = p
            val off = p
            val neg = a[p] == '-'.code.toByte()
            if (neg || a[p] == '+'.code.toByte()) p++

            var m: Long = 0
            var exp = 0
            var d = 0

            while (p < end && ((a[p] - '0'.code.toByte()).also { d = it }) >= 0 && d < 10) {
                if (m < MANTISSA_LIMIT) m = m * 10 + d
                else if (exact) return slowParseDouble(a, off, end)
                else exp++
                p++
            }

            if (p < end && a[p] == '.'.code.toByte()) {
                p++
                while (p < end && ((a[p] - '0'.code.toByte()).also { d = it }) >= 0 && d < 10) {
                    if (m < MANTISSA_LIMIT) {
                        m = m * 10 + d
                        exp--
                    } else if (exact) return slowParseDouble(a, off, end)
                    p++
                }
            }

            if (p < end && (a[p].toInt() or 0x20) == 'e'.code) {
                var eneg = false
                if (++p < end && (a[p] == '-'.code.toByte() || a[p] == '+'.code.toByte())) eneg =
                    a[p++] == '-'.code.toByte()
                var e = 0
                while (p < end && ((a[p] - '0'.code.toByte()).also { d = it }) >= 0 && d < 10) {
                    if (e < 100000) e = e * 10 + d
                    p++
                }
                exp += if (eneg) -e else e
            }

            // Leftover characters (NaN, Infinity, hex, type suffixes...) or an exponent out of range
            if (p != end || exp < -22 || exp > 22) return slowParseDouble(a, off, end)

            val v: Double
            if (exp == 0) v = m.toDouble()
            else if (m < (1L shl 53) || !exact) v = if (exp < 0) m / POW10[-exp] else m * POW10[exp]
            else return slowParseDouble(a, off, end)

            return if (neg) -v else v
        }

        private fun slowParseDouble(a: ByteArray, off: Int, end: Int): Double {
            return String(a, off, end - off, LATIN1).toDouble()
        }
    }
}