import Instruction.LEFT
import Instruction.RIGHT
import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import kotlin.text.RegexOption.MULTILINE


private enum class Instruction {
    LEFT, RIGHT
}

fun main() {
    data class Node(val source: String, val left: String, val right: String)
    data class GameMap(val instructions: Sequence<Instruction>, val nodes: Map<String, Node>)


    val parser = object : Grammar<GameMap>() {
        val ` ` by literalToken(" ")
        val `(` by literalToken("(")
        val `)` by literalToken(")")
        val `,` by literalToken(",")
        val `=` by literalToken("=")
        val NL by literalToken("\n")
        val INSTR by regexToken(Regex("^[RL]+$", MULTILINE))
        val WORD by regexToken("[\\dA-Z]{3}")

        val instruction by INSTR * -NL use {
            val source = text.map { if (it == 'L') LEFT else RIGHT }
            sequence {
                while (true) {
                    for (c in source) {
                        yield(c)
                    }
                }
            }
        }
        val node by WORD * -` ` * -`=` * -` ` * -`(` * WORD * -`,` * -` ` * WORD * -`)` use {
            Node(t1.text, t2.text, t3.text)
        }
        val nodes by separated(node, NL) use { terms.associateBy { it.source } }
        override val rootParser by instruction * -NL * nodes use { GameMap(t1, t2) }
    }


    fun part1(test: GameMap): Int {
        val map = test.nodes
        var cur = map["AAA"]!!
        var counter = 0
        for (instruction in test.instructions) {
            if (cur.source == "ZZZ") break
            cur = when (instruction) {
                LEFT -> map[cur.left]!!
                RIGHT -> map[cur.right]!!
            }
            counter++
        }
        return counter
    }

    fun lcmFor2(a: Long, b: Long): Long {
        val larger = if (a > b) a else b
        val maxLcm = a * b
        var lcm = larger
        while (lcm <= maxLcm) {
            if (lcm % a == 0L && lcm % b == 0L) return lcm
            lcm += larger
        }
        return maxLcm
    }

    fun lcm(loopLengths: ArrayList<Long>): Long {
        var lcm = 1L
        for (number in loopLengths) {
            lcm = lcmFor2(lcm, number)
        }
        return lcm
    }

    fun part2(test: GameMap): Long {
        val map = test.nodes
        val cur = map.filter { it.key.endsWith('A') }
        val loopLengths = arrayListOf<Long>()
        for (aSource in cur.keys) {
            var tmp = map[aSource]!!
            var counter = 0L
            for (instruction in test.instructions) {
                if (tmp.source.endsWith('Z')) {
                    loopLengths.add(counter)
                    break
                }
                tmp = when (instruction) {
                    LEFT -> map[tmp.left]!!
                    RIGHT -> map[tmp.right]!!
                }
                counter++
            }
        }
        return lcm(loopLengths)
    }

    val test = parser.parseToEnd(readInputTxt("08t1"))
    val test2 = parser.parseToEnd(readInputTxt("08t2"))
    val test3 = parser.parseToEnd(readInputTxt("08t3"))
    val input = parser.parseToEnd(readInputTxt("08"))
    check(part1(test) == 2)
    check(part1(test2) == 6)
    part1(input).println()
    check(part2(test3) == 6L)
    part2(input).println()
}


