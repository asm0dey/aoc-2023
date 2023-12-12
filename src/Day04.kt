import kotlin.math.pow
import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken

fun main() {
    data class Game(val num: Int, val intersectionSize: Int)

    val parser = object : Grammar<List<Game>>() {
        val colon by literalToken(":")
        val cardLit by literalToken("Card")
        val sp by regexToken(" +")
        val pipe by literalToken("|")
        val num by regexToken("\\d+")
        val nl by literalToken("\n")
        val numList by separated(num, sp) map { it.map { it.text.toInt() }.toSet() }
        val cardName by -cardLit * -sp * num * -colon * -sp map { it.text.toInt() }
        val sep by sp * pipe * sp
        val game by cardName * numList * -sep * numList map { Game(it.t1, it.t2.intersect(it.t3).size) }
        override val root by separated(game, nl)
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

    val test = parser.parse(readInputTxt("04t1")).getOrThrow()
    check(part1(test) == 13)
    val input = parser.parse(readInputTxt("04")).getOrThrow()
    part1(input).println()
    check(part2(test) == 30)
    part2(input).println()
}
