import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import kotlin.math.pow

fun main() {
    data class Game(val num: Int, val intersectionSize: Int)

    val parser = object : Grammar<List<Game>>() {
        val colon by literalToken(":")
        val cardLit by literalToken("Card")
        val sp by regexToken(" +")
        val pipe by literalToken("|")
        val num by regexToken("\\d+")
        val nl by literalToken("\n")
        val numList by separated(num, sp) use { terms.map { it.text.toInt() }.toSet() }
        val cardName by -cardLit * -sp * num * -colon * -sp use { text.toInt() }
        val sep by sp * pipe * sp
        val game by cardName * numList * -sep * numList use { Game(t1, t2.intersect(t3).size) }
        override val rootParser by separated(game, nl) use { terms }
    }

    fun part1(input: List<Game>): Int = input.sumOf { (_, b) -> 2.0.pow(b - 1).toInt() }

    fun part2(input: List<Game>): Int {
        val map = input.groupBy { it.num }
            .mapValues {
                val game = it.value.first()
                it.value.size to game.intersectionSize
            }
            .toSortedMap()
        map.keys.forEach { current ->
            val (amount, intersectionSize) = map[current]!!
            repeat(intersectionSize) {
                val next = current + it + 1
                map[next] = map[next]!!.copy(first = map[next]!!.first + amount)
            }
        }
        return map.values.sumOf { it.first }
    }

    val test = parser.parseToEnd(readInputTxt("04t1"))
    check(part1(test) == 13)
    val input = parser.parseToEnd(readInputTxt("04"))
    part1(input).println()
    check(part2(test) == 30)
    part2(input).println()
}
