import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken


fun main() {

    data class Hand(val cards: List<Char>, val bid: Int) {
        fun combination(withJokers: Boolean = false): Int {
            val groups = cards.groupBy { it }.mapValues { it.value.size }.toMutableMap()
            if (withJokers && 'J' in groups.keys && groups.size > 1) {
                val maxK = groups.filter { it.key != 'J' }.maxBy { it.value }.key
                groups[maxK] = groups[maxK]!! + groups['J']!!
                groups.remove('J')
            }
            return when (groups.size) {
                1 -> 7
                2 -> if (groups.values.any { it == 4 }) 6 else 5
                3 -> if (groups.values.any { it == 3 }) 4 else 3
                4 -> 2
                5 -> 1
                else -> error("What the heck is $cards?")
            }
        }
    }

    data class Game(val hands: List<Hand>)

    fun compare(pairs: List<Pair<Char, Char>>, withJokers: Boolean = false): Int {
        val order = (if (!withJokers) "AKQJT98765432" else "AKQT98765432J").reversed()
        for ((f, s) in pairs) {
            val diff = order.indexOf(f) - order.indexOf(s)
            if (diff == 0) continue
            return diff
        }
        return 0
    }

    val parser = object : Grammar<Game>() {
        val sp by regexToken(" +")
        val one by regexToken("[01]")
        val num by regexToken("[2-9]")
        val card by regexToken("[AKQJT]")
        val nl by literalToken("\n")

        val cardSet by 5 times (card or num) use { map { it.text.first() } }
        val hand by cardSet * -sp * oneOrMore(one or num) use { Hand(t1, t2.joinToString("") { it.text }.toInt()) }
        override val rootParser by separated(hand, nl) use { Game(terms) }
    }


    fun part1(input: Game): Long {
        return input.hands
                .sortedWith { o1, o2 ->
                    (o1.combination() - o2.combination()).takeIf { it != 0 } ?: compare(o1.cards.zip(o2.cards))
                }
                .asSequence().zip((1..Int.MAX_VALUE).asSequence())
                .sumOf { (a, b) -> a.bid.toLong() * b }
    }


    fun part2(input: Game): Long {
        return input.hands
                .sortedWith { o1, o2 ->
                    (o1.combination(withJokers = true) - o2.combination(withJokers = true))
                            .takeIf { it != 0 } ?: compare(o1.cards.zip(o2.cards), withJokers = true)
                }
                .asSequence().zip((1..Int.MAX_VALUE).asSequence())
                .sumOf { (a, b) -> a.bid.toLong() * b }
    }

    val test = parser.parseToEnd(readInputTxt("07t1"))
    check(part1(test) == 6440L)
    val input = parser.parseToEnd(readInputTxt("07"))
    part1(input).println()
    check(part2(test) == 5905L)
    part2(input).println()
}


