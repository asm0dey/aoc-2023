import kotlin.math.abs

fun main() {
    fun <T> List<T>.pairwise() = sequence {
        for (i in indices) {
            for (j in i + 1..indices.last) {
                yield(get(i) to get(j))
            }
        }
    }

    fun solve(input: List<String>, expansionRate: Int): Long {
        val galaxies = input
                .flatMapIndexed { y, line ->
                    line.mapIndexedNotNull { x, c -> if (c == '#') x to y else null }
                }
        val freeLines = (galaxies.minOf { it.second }..galaxies.maxOf { it.second })
                .filter { col -> galaxies.none { it.second == col } }
        val freeCols = (galaxies.minOf { it.first }..galaxies.maxOf { it.first })
                .filter { row -> galaxies.none { it.first == row } }
        return galaxies
                .pairwise()
                .sumOf { (a, b) ->
                    val (x1, y1) = a
                    val (x2, y2) = b
                    val expandedXCount = freeCols.count { it in minOf(x1, x2)..maxOf(x1, x2) }
                    val expandedYCount = freeLines.count { it in minOf(y1, y2)..maxOf(y1, y2) }
                    (expansionRate - 1L) * (expandedXCount + expandedYCount) + abs(y1 - y2) + abs(x1 - x2)
                }
    }

    val test1 = readInput("11t1")
    val input = readInput("11")
    check(solve(test1, 2) == 374L)
    solve(input, 2).println()
    check(solve(test1, 10) == 1030L)
    solve(input, 1000000).println()
}




