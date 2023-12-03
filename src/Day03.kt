fun main() {
    fun part1(input: List<String>): Int {
        val symbols = input
            .mapIndexed { index, line ->
                index to line
                    .mapIndexed { ci, c -> ci to c }
                    .filter { (_, c) -> !c.isDigit() && c != '.' }
                    .map { it.first }
                    .toSet()
            }
            .toMap()
        return input
            .mapIndexed { index, line ->
                index to Regex("\\d+")
                    .findAll(line)
                    .map { (it.range.first - 1..it.range.last + 1) to it.value.toInt() }
                    .toList()
            }
            .sumOf { (line, matches) ->
                matches.sumOf { match ->
                    val (range, value) = match
                    val symbolsOnLines =
                        (symbols[line - 1] ?: setOf()) + (symbols[line] ?: setOf()) + (symbols[line + 1] ?: setOf())
                    range.firstOrNull { it in symbolsOnLines }?.let { value } ?: 0
                }
            }
    }

    fun part2(input: List<String>): Long {
        val parts = input
            .mapIndexed { index, line ->
                index to Regex("\\d+")
                    .findAll(line)
                    .map { (it.range.first - 1..it.range.last + 1) to it.value.toInt() }
                    .toList()
            }
            .toMap()
        return input
            .mapIndexed { index, line ->
                index to line
                    .mapIndexed { ci, c -> ci to c }
                    .filter { (_, c) -> c == '*' }
                    .map { it.first }
            }
            .sumOf { (y, xs) ->
                val partsNearby = (parts[y - 1] ?: listOf()) + (parts[y] ?: listOf()) + (parts[y + 1] ?: listOf())
                xs
                    .filter { x ->
                        partsNearby.count { x in it.first } == 2
                    }
                    .sumOf { x ->
                        partsNearby
                            .filter { x in it.first }
                            .fold(1L) { acc, next -> next.second.toLong() * acc }
                    }
            }
    }

    val test = readInput("03t1")
    check(part1(test) == 4361)
    val input = readInput("03")
    part1(input).println()
    check(part2(test) == 467835L)
    part2(input).println()
}
