import me.alllex.parsus.parser.*
import me.alllex.parsus.token.EofToken
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken

fun main() {
    data class Pull(val text: String, val amount: Int)
    data class Round(val pulls: List<Pull>)
    data class Game(val num: Int, val rounds: List<Round>)

    val parser = object : Grammar<List<Game>>() {
        val colon by literalToken(":")
        val comma by literalToken(",")
        val gameLit by literalToken("Game")
        val nl by literalToken("\n")
        val semi by literalToken(";")
        val sp by literalToken(" ")
        val num by regexToken("\\d+")
        val red by literalToken("red")
        val green by literalToken("green")
        val blue by literalToken("blue")
        val color by red or green or blue map { it.text }
        val name by -gameLit * -sp * num * -colon * -sp
        val pull by (num * -sp * color) map { Pull(it.t2, it.t1.text.toInt()) }
        val round by separated(pull, comma * sp) map { Round(it) }
        val game by name * separated(round, semi * sp) map { Game(it.t1.text.toInt(), it.t2) }
        override val root by separated(game, nl) * -EofToken
    }

    fun part1(input: List<Game>): Int = input
            .filter { (_, game) ->
                game.all { round ->
                    round.pulls.all {
                        when (it.text) {
                            "red" -> it.amount <= 12
                            "green" -> it.amount <= 13
                            "blue" -> it.amount <= 14
                            else -> false
                        }
                    }
                }
            }
            .sumOf { it.num }

    fun part2(input: List<Game>): Int = input.sumOf {
        it
                .rounds
                .flatMap(Round::pulls)
                .groupBy(Pull::text)
                .values
                .map { it.maxOf(Pull::amount) }
                .reduce(Int::times)
    }

    val test = parser.parseOrThrow(readInputTxt("02t1"))
    part1(test).println()
    val input = parser.parseOrThrow(readInputTxt("02"))
    part1(input).println()
    part2(test).println()
    part2(input).println()
}
