import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.st.LiftToSyntaxTreeOptions
import com.github.h0tk3y.betterParse.st.liftToSyntaxTreeGrammar

fun main() {
    data class Pull(val text: String, val amount: Int)
    data class Round(val pulls: List<Pull>)
    data class Game(val num: Int, val rounds: List<Round>)

    val parser = object : Grammar<List<Game>>() {
        val colon by literalToken(":", ignore = true)
        val comma by literalToken(",", ignore = true)
        val gameLit by literalToken("Game", ignore = true)
        val nl by literalToken("\n", ignore = true)
        val semi by literalToken(";", ignore = true)
        val sp by literalToken(" ", ignore = true)
        val num by regexToken("\\d+")
        val red by literalToken("red")
        val green by literalToken("green")
        val blue by literalToken("blue")
        val color by red or green or blue use { text }
        val name by -gameLit * -sp * num * -colon * -sp use { text.toInt() }
        val pull by (num * -sp * color) use { Pull(t2, t1.text.toInt()) }
        val round by separated(pull, comma * sp) use { Round(terms) }
        val game by name * separated(round, semi * sp) use { Game(t1, t2.terms) }
        override val rootParser by separated(game, nl) use { terms }
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

    val test = parser.parseToEnd(readInputTxt("02t1"))
    part1(test).println()
    val input = parser.parseToEnd(readInputTxt("02"))
    part1(input).println()
    part2(test).println()
    part2(input).println()
}
