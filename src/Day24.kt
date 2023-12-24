import org.apache.commons.math4.legacy.linear.MatrixUtils
import org.apache.commons.math4.legacy.linear.RealMatrix
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.sign

fun main() {
    data class P2(val x: Long, val y: Long)
    data class P3(val x: Long, val y: Long, val z: Long)

    operator fun P2.plus(diff: P2): P2 = P2(x + diff.x, y + diff.y)
    operator fun P3.plus(diff: P3): P3 = P3(x + diff.x, y + diff.y, z + diff.z)
    operator fun P3.minus(diff: P3): P3 = P3(x - diff.x, y - diff.y, z - diff.z)

    fun part1(input: String, start: Double, end: Double) {
        val hailstones = input
            .split(*" ,@\n".toCharArray())
            .filter { it.isNotBlank() }
            .chunked(3) { (a, b, _) -> P2(a.toLong(), b.toLong()) }
            .chunked(2) { (a, b) -> a to b }

        val coefs = hailstones.map { (p1, diff) ->
            val p2 = p1 + diff
            val dy = p2.y - p1.y
            val dx = p2.x - p1.x
            val k = dy.toDouble() / dx
            val b = p1.y - k * p1.x
            println("y = ${k}x+$b")
            k to b
        }
        var counter = 0L
        for (i in coefs.indices) {
            val k = coefs[i].first
            val b = coefs[i].second
            for (j in i + 1 until coefs.size) {
                val k1 = coefs[j].first
                val b1 = coefs[j].second
                val x = (b1 - b) / (k - k1)
                if (x.isInfinite()) {
                    continue // parallel
                }
                val y = k * x + b
                if ((x - hailstones[i].first.x).sign.toInt() != hailstones[i].second.x.sign ||
                    (x - hailstones[j].first.x).sign.toInt() != hailstones[j].second.x.sign
                ) {
                    continue // In the past
                }
                if (x in start..end && y in start..end) {
                    counter++
                }
                // else missed from the test area
            }
        }
        println(counter)
    }

    fun RealMatrix.inverse() = MatrixUtils.inverse(this)
    operator fun RealMatrix.times(other: RealMatrix) = multiply(other)
    fun matrixOf(vararg ar: DoubleArray) = MatrixUtils.createRealMatrix(ar)

    fun part2(input: String) {

        val hailstones = input
            .split(*" ,@\n".toCharArray())
            .filter { it.isNotBlank() }
            .chunked(3) { (a, b, c) -> P3(a.toLong(), b.toLong(), c.toLong()) }
            .chunked(2) { (a, b) -> a to b }


        fun getPosAndTime(a: Int, b: Int): DoubleArray {
            val p1 = hailstones[a].first - hailstones[0].first
            val p2 = hailstones[b].first - hailstones[0].first
            val v1 = hailstones[a].second - hailstones[0].second
            val v2 = hailstones[b].second - hailstones[0].second
            val matrix = matrixOf(
                doubleArrayOf(p1.x.toDouble(), v1.x.toDouble(), -v2.x.toDouble()),
                doubleArrayOf(p1.y.toDouble(), v1.y.toDouble(), -v2.y.toDouble()),
                doubleArrayOf(p1.z.toDouble(), v1.z.toDouble(), -v2.z.toDouble()),
            )
            val vector = matrixOf(
                doubleArrayOf(p2.x.toDouble()),
                doubleArrayOf(p2.y.toDouble()),
                doubleArrayOf(p2.z.toDouble()),
            )
            val res = matrix.inverse() * vector
            val t2 = res.data[2][0]
            val pos2x = hailstones[b].first.x + t2 * hailstones[b].second.x
            val pos2y = hailstones[b].first.y + t2 * hailstones[b].second.y
            val pos2z = hailstones[b].first.z + t2 * hailstones[b].second.z
            return doubleArrayOf(t2, pos2x, pos2y, pos2z)
        }

        val (t1, x1, y1, z1) = getPosAndTime(1, 2)
        val (t2, x2, y2, z2) = getPosAndTime(1, 3)
        val vx = (x2 - x1) / (t2 - t1)
        val vy = (y2 - y1) / (t2 - t1)
        val vz = (z2 - z1) / (t2 - t1)
        val x = x1 - t1 * vx
        val y = y1 - t1 * vy
        val z = z1 - t1 * vz
        println(DecimalFormat("#").format(x + y + z))

    }
    part1(readInputTxt("24t1"), 7.0, 27.0)
    part1(readInputTxt("24"), 200000000000000L.toDouble(), 400000000000000L.toDouble())
    part2(readInputTxt("24t1"))
    part2(readInputTxt("24"))
}

