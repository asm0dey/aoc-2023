import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken


fun main() {

    val parser = object : Grammar<List<List<Long>>>() {
        val ` ` by literalToken("space", " ")
        val num by regexToken("-?\\d+")
        val NL by literalToken("\n")

        val lst by separated(num, ` `) use { terms.map { it.text.toLong() } }
        override val rootParser by separated(lst, NL) use { terms }
    }

    fun List<Long>.nextLevel() = windowed(2).map { (a, b) -> b - a }

    fun List<Long>.extrapolateRight(): Long =
            if (all { it == 0L }) 0 else last() + nextLevel().extrapolateRight()

    fun List<Long>.extrapolateLeft(): Long =
            if (all { it == 0L }) 0 else first() - nextLevel().extrapolateLeft()

    fun List<List<Long>>.solve(op: List<Long>.() -> Long = List<Long>::extrapolateRight) = sumOf(op)

    val test = parser.parseToEnd(readInputTxt("09t1"))
    check(test.solve() == 114L)
    val input = parser.parseToEnd(readInputTxt("09"))
    input.solve().println()
    check(test.solve(List<Long>::extrapolateLeft) == 2L)
    input.solve(List<Long>::extrapolateLeft).println()
}


