import Day12.*
import arrow.core.MemoizedDeepRecursiveFunction
import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken

private enum class Day12(val txt: String) {
    OK("."), DAMAGED("#"), UNKNOWN("?");

    override fun toString(): String = txt
}

private val x = List(11) { OK }

fun main() {
    data class Line(val states: List<Day12>, val amounts: List<Int>) {
        override fun toString(): String {
            return states.joinToString("") + " " + amounts.joinToString(",")
        }
    }

    val parser = object : Grammar<List<Line>>() {
        val ok by literalToken(".")
        val damaged by literalToken("#")
        val unknown by literalToken("?")
        val sp by regexToken(" +", "space")
        val num by regexToken("\\d+")
        val comma by literalToken(",")
        val nl by literalToken("\n")
        val line by oneOrMore(ok or damaged or unknown) * -sp * separated(num, comma) map { (x, y) ->
            val states = x.map {
                when (it.text) {
                    "." -> OK
                    "#" -> DAMAGED
                    "?" -> UNKNOWN
                    else -> error("Unknown state ${it.text}")
                }
            }
            val amounts = y.map { it.text.toInt() }
            Line(states, amounts)
        }
        override val root by separated(line, nl)
    }

    data class SearchState(val currentPosition: Int, val currentGroupIdx: Int, val currentGroupLength: Int) {
        override fun toString() = "SearchState(pos=$currentPosition, groupIdx=$currentGroupIdx, curGrpLen=$currentGroupLength)"
    }


    fun countPermutations(it: Line): Long {
        val (states, groups) = it
        val countValidPermutations: DeepRecursiveFunction<SearchState, Long> = MemoizedDeepRecursiveFunction { (currentPosition, currentGroupIdx, currentGroupLength) ->
            suspend fun DeepRecursiveScope<SearchState, Long>.countDotPermutations() = when {
                currentGroupLength == 0 ->
                    callRecursive(SearchState(currentPosition = currentPosition + 1, currentGroupIdx = currentGroupIdx, currentGroupLength = 0))

                currentGroupIdx < groups.size && currentGroupLength == groups[currentGroupIdx] ->
                    callRecursive(SearchState(currentPosition = currentPosition + 1, currentGroupIdx = currentGroupIdx + 1, currentGroupLength = 0))

                else -> 0L
            }

            suspend fun DeepRecursiveScope<SearchState, Long>.countHashPermutations() =
                    callRecursive(SearchState(currentPosition = currentPosition + 1, currentGroupIdx = currentGroupIdx, currentGroupLength = currentGroupLength + 1))

            if (currentPosition == states.size) // end of the source
                when {
                    currentGroupIdx == groups.size - 1 && currentGroupLength == groups[currentGroupIdx] -> 1L // hash in the end
                    currentGroupIdx == groups.size && currentGroupLength == 0 -> 1L// dot in the end
                    else -> 0L
                }
            else
                when (states[currentPosition]) {
                    OK -> countDotPermutations()
                    DAMAGED -> countHashPermutations()
                    UNKNOWN -> countDotPermutations() + countHashPermutations()
                }

        }
        val result = countValidPermutations(SearchState(currentPosition = 0, currentGroupIdx = 0, currentGroupLength = 0))
        return result
    }

    fun part1(test: List<Line>): Long {
        return test.sumOf(::countPermutations)
    }

    fun part2(test: List<Line>): Long = test
            .sumOf { (a, b) ->
                val line = Line(
                        (1..5).map { a }.reduce { acc, day12s -> acc + UNKNOWN + day12s },
                        (1..5).map { b }.reduce { acc, longs -> acc + longs }
                )
                countPermutations(line).also { println("$a $b $it") }
            }


    val test = parser.parseOrThrow(readInputTxt("12t1"))
    check(part1(test) == 21L)
    val input = parser.parseOrThrow(readInputTxt("12"))
    part1(input).println()
    check(part2(test) == 525152L)
    part2(input).println()
}

