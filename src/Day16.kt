data class Point(val x: Int, val y: Int) {
    constructor(x: Pair<Int, Int>) : this(x.first, x.second)
}

operator fun Point.minus(other: Point) = Point(x - other.x, y - other.y)

operator fun Point.plus(other: Point) = Point(x + other.x, y + other.y)
operator fun Point.times(amount: Int) = Point(x * amount, y * amount)

private fun Array<CharArray>.isSafe(at: Point) =
    at.y in indices && at.x in this[at.y].indices

operator fun Array<CharArray>.get(at: Point): Char =
    this[at.y][at.x]

val UP = Point(0, -1)
val LEFT = Point(-1, 0)
val DOWN = Point(0, 1)
val RIGHT = Point(1, 0)

fun main() {
    val movements = mapOf(
        '-' to UP to listOf(LEFT, RIGHT),
        '-' to DOWN to listOf(LEFT, RIGHT),
        '|' to LEFT to listOf(UP, DOWN),
        '|' to RIGHT to listOf(UP, DOWN),
        '\\' to UP to listOf(RIGHT),
        '\\' to RIGHT to listOf(UP),
        '\\' to DOWN to listOf(LEFT),
        '\\' to LEFT to listOf(DOWN),
        '/' to UP to listOf(LEFT),
        '/' to RIGHT to listOf(DOWN),
        '/' to DOWN to listOf(RIGHT),
        '/' to LEFT to listOf(UP)
    )

    fun energize(startPoint: Point, startDirection: Point, grid: Array<CharArray>): Int {
        val seen = hashSetOf(startPoint to startDirection)
        val queue = ArrayDeque(listOf(startPoint to startDirection))
        while (queue.isNotEmpty()) {
            val (place, direction) = queue.removeFirst()
            val nextDirections = movements[grid[place] to direction] ?: listOf(direction)
            nextDirections.forEach { nextDirection ->
                val nextPlace = place + nextDirection
                val nextPair = nextPlace to nextDirection
                if (nextPair !in seen && grid.isSafe(nextPlace)) {
                    queue.add(nextPair)
                    seen += nextPair
                }
            }
        }

        return seen.map { it.first }.toSet().size
    }

    fun List<String>.grid(): Array<CharArray> = map { it.toCharArray() }.toTypedArray<CharArray>()

    fun part1(input: List<String>): Int = energize(Point(0, 0), LEFT, input.grid())

    fun part2(input: List<String>): Int {
        val grid = input.grid()
        return sequenceOf(
            grid.first().indices.asSequence().map { Point(it, 0) to DOWN },
            grid.first().indices.asSequence().map { Point(it, grid.lastIndex) to UP },
            grid.indices.asSequence().map { Point(0, it) to LEFT },
            grid.indices.asSequence().map { Point(grid.first().lastIndex, it) to RIGHT }
        )
            .flatten()
            .maxOf { energize(it.first, it.second, grid) }
    }


    println(part1(readInput("16")))
    println(part2(readInput("16")))
}
