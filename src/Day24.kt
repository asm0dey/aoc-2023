import kotlin.math.sign

fun main() {
    data class P2(val x: Long, val y: Long)
    data class P3(val x: Long, val y: Long, val z: Long)

    operator fun P2.plus(diff: P2): P2 = P2(x + diff.x, y + diff.y)
    operator fun P3.plus(diff: P3): P3 = P3(x + diff.x, y + diff.y, z + diff.z)

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
                val k2 = coefs[j].first
                val b2 = coefs[j].second
                val x = (b2 - b) / (k - k2)
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
    part1(readInputTxt("24t1"), 7.0, 27.0)
    part1(readInputTxt("24"), 200000000000000L.toDouble(), 400000000000000L.toDouble())
}

