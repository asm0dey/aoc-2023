fun main() {

    fun part1(input: List<String>): Int = input
        .asSequence()
        .map { it.split(',', ' ', ':', ';').filterNot { it.isBlank() }.chunked(2) }
        .map {
            it.first()[1].toInt() to it.drop(1)
        }
        .filter { (_, dice) ->
            dice.all { (num, color) ->
                when (color) {
                    "red" -> num.toInt() <= 12
                    "green" -> num.toInt() <= 13
                    "blue" -> num.toInt() <= 14
                    else -> false
                }
            }
        }
        .sumOf { it.first }

    fun part2(input: List<String>): Int = input
        .asSequence()
        .map { it.split(',', ' ', ':', ';').filterNot { it.isBlank() }.chunked(2) }
        .map {
            it.first()[1].toInt() to it.drop(1)
        }
        .sumOf { (_, games) ->
            games.filter { it[1] == "red" }.maxOf { it[0].toInt() } *
                    games.filter { it[1] == "green" }.maxOf { it[0].toInt() } *
                    games.filter { it[1] == "blue" }.maxOf { it[0].toInt() }
        }

    val test = readInput("02t1")
    part1(test).println()
    val input = readInput("02")
    part1(input).println()
    part2(test).println()
    part2(input).println()
}
