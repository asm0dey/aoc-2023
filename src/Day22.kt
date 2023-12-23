import arrow.optics.copy
import arrow.optics.optics


fun main() {
    fun Cuboid.moveDown() = copy {
        Cuboid.p1.z set (p1.z - 1)
        Cuboid.p2.z set (p2.z - 1)
    }

    fun Cuboid.nextPos(cuboids: Collection<Cuboid>): Cuboid {
        val next = moveDown()
        if (next.p1.z <= 0) return this
        for (cuboid in cuboids) {
            if (this == cuboid) continue
            if (next.intersects(cuboid)) return this
        }
        return next
    }

    fun List<String>.parse() = flatMap { it.split(',', '~') }
        .chunked(3) { (a, b, c) -> Point3(a.toLong(), b.toLong(), c.toLong()) }
        .chunked(2) { (a, b) -> Cuboid(a, b) }

    fun List<Cuboid>.oneStep(): List<Cuboid> {
        val source = toMutableList()
        for ((index, cuboid) in source.withIndex()) {
            source[index] = cuboid.nextPos(source)
        }
        return source.toList()
    }

    fun List<Cuboid>.fall(): List<Cuboid> {
        var result = this
        while (true) {
            val next = result.oneStep()
            if (next == result) break
            else result = next
        }
        return result
    }

    fun Cuboid.findDependants(final: Set<Cuboid>): List<Cuboid> {
        val updated = (final - this).toList()
        return updated.oneStep().filterIndexed { index, cuboid ->
            updated[index] != cuboid
        }
    }

    fun solve(input: List<String>) {
        val cuboids = input.parse().sortedBy { it.p1.z }.fall().toSet()
        val res = cuboids.map { it.findDependants(cuboids) }
        val a = res.count { it.isEmpty() }
        val b = res.sumOf { it.size }
        println("$a $b")
    }

    val test = readInput("22t1")
    solve(test)
    val input = readInput("22")
    solve(input)
}

@optics
data class Point3(val x: Long, val y: Long, val z: Long) {
    companion object
}

@optics
data class Cuboid(val p1: Point3, val p2: Point3) {
    companion object
}


operator fun Point3.minus(other: Point3) = Point3(x - other.x, y - other.y, z - other.z)
operator fun Point3.plus(other: Point3) = Point3(x + other.x, y + other.y, z + other.z)
operator fun Cuboid.contains(point: Point3) = point.x in p1.x..p2.x &&
        point.y in p1.y..p2.y &&
        point.z in p1.z..p2.z

fun Cuboid.intersects(other: Cuboid) = !(
        p2.x !in other.p1.x..other.p2.x &&
                other.p2.x !in p1.x..p2.x ||
                p2.y !in other.p1.y..other.p2.y &&
                other.p2.y !in p1.y..p2.y ||
                p2.z !in other.p1.z..other.p2.z &&
                other.p2.z !in p1.z..p2.z
        )

val Cuboid.volume
    get() = (p1 - p2).product()

private fun Point3.product() = x * y * z
