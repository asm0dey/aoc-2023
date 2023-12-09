import com.github.h0tk3y.betterParse.combinators.separated
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.combinators.unaryMinus
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.sqrt


fun main() {

    val parser = object : Grammar<Pair<List<Long>, List<Long>>>() {
        val colon by literalToken(":")
        val sp by regexToken(" +")
        val num by regexToken("\\d+")
        val nl by literalToken("\n")
        val timeLit by literalToken("Time")
        val distLit by literalToken("Distance")
        val times by -timeLit * -colon * -sp * separated(num, sp) use { terms.map { it.text.toLong() } }
        val distances by -distLit * -colon * -sp * separated(num, sp) use { terms.map { it.text.toLong() } }
        override val rootParser by times * -nl * distances use { t1 to t2 }
    }

    fun solve(a: Long, b: Long): Long {
        // x * (a - x) > b
        // x^2 - ax + b < 0
        //  1/2 (a - sqrt(a^2 - 4 b))<x<1/2 (sqrt(a^2 - 4 b) + a)
        val start = ((a - sqrt(a.toDouble().pow(2) - b * 4)) / 2) + .00000000001
        val end = ((a + sqrt(a.toDouble().pow(2) - b * 4)) / 2) - .000000000001
        val up = DecimalFormat("#").apply { roundingMode = RoundingMode.UP }
        val down = DecimalFormat("#").apply { roundingMode = RoundingMode.DOWN }
        return down.format(end).toLong() - up.format(start).toLong() + 1
    }


    fun part1(input: Pair<List<Long>, List<Long>>): Long {
        return input.first.zip(input.second)
                .map { (a, b) -> solve(a, b) }
                .reduce { acc, l -> acc * l }
    }


    fun part2(input: Pair<List<Long>, List<Long>>): Long {
        val a = input.first.joinToString("") { it.toString() }.toLong()
        val b = input.second.joinToString("") { it.toString() }.toLong()
        return solve(a, b)
    }

    val test = parser.parseToEnd(readInputTxt("06t1"))
    check(part1(test) == 288L)
    val input = parser.parseToEnd(readInputTxt("06"))
    part1(input).println()
    check(part2(test) == 71503L)
    part2(input).println()
}
