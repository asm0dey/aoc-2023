import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken


fun main() {

    val parser = object : Grammar<List<List<Long>>>() {
        val ` ` by literalToken(" ", "space")
        val num by regexToken("-?\\d+")
        val NL by literalToken("\n")

        val lst by separated(num, ` `) map { it.map { it.text.toLong() } }
        override val root by separated(lst, NL)
    }

    fun List<Long>.nextLevel() = windowed(2).map { (a, b) -> b - a }

    fun List<Long>.extrapolateRight(): Long =
            if (all { it == 0L }) 0 else last() + nextLevel().extrapolateRight()

    fun List<Long>.extrapolateLeft(): Long =
            if (all { it == 0L }) 0 else first() - nextLevel().extrapolateLeft()

    fun List<List<Long>>.solve(op: List<Long>.() -> Long = List<Long>::extrapolateRight) = sumOf(op)

    val test = parser.parseOrThrow(readInputTxt("09t1"))
    check(test.solve() == 114L)
    val input = parser.parseOrThrow(readInputTxt("09"))
    input.solve().println()
    check(test.solve(List<Long>::extrapolateLeft) == 2L)
    input.solve(List<Long>::extrapolateLeft).println()
}


