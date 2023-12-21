fun Point.neighbors() = listOf(UP, DOWN, LEFT, RIGHT).map { it + this }

fun main() {
    fun List<String>.findStart() = indices
            .flatMap { i ->
                indices
                        .filter { j -> this[i][j] == 'S' }
                        .map { j -> Point(i, j) }
            }
            .single()

    fun part1(input: List<String>) {
        var work = setOf(input.findStart())
        repeat(64) {
            work = work
                    .flatMap { it.neighbors() }
                    .filter { it.x in input.indices && it.y in input.indices && input[it.x][it.y] != '#' }
                    .toSet()
        }

        println(work.size)
    }

    fun part2(input: List<String>) {
        val gridSize = if (input.size == input[0].length) input.size else error("Works only for quadratic grid")

        val grids = 26501365 / gridSize
        val rem = 26501365 % gridSize

        val seq = mutableListOf<Int>()
        var work = setOf(input.findStart())
        var steps = 0
        repeat(3) { n ->
            while (steps < n * gridSize + rem) {
                work = work
                        .flatMap { it.neighbors() }
                        .filter { input[(it.x % gridSize + gridSize) % gridSize][(it.y % gridSize + gridSize) % gridSize] != '#' }
                        .toSet()
                steps++
            }
            seq.add(work.size)
        }

        val c = seq[0]
        val a = (seq[2].toLong() - c - (2 * (seq[1] - c))) / 2
        val b = seq[1] - c - a

        val x = grids.toLong()

        println(a * (x * x) + b * x + c)
    }
    part1(readInput("21"))
    part2(readInput("21"))
}
