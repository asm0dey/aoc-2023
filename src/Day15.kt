import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken

fun main() {
    fun part1(input: String): Int {
        val parser = object : Grammar<Int>() {
            init {
                literalToken("\n", ignored = true)
            }

            val comma by literalToken(",")
            val char by regexToken("[^,]")
            val instr by oneOrMore(char) map {
                it.map { it.text[0].code }.fold(0) { acc, v ->
                    ((acc + v) * 17) % 256
                }
            }
            val instructions by separated(instr, comma) map {
                it.sum()
            }
            override val root by instructions
        }
        return parser.parseOrThrow(input)
    }

    fun part2(input: String): Int {
        val parser = object : Grammar<Int>() {

            val comma by literalToken(",")
            val num by regexToken("\\d+")
            val eq by literalToken("=")
            val dash by literalToken("-")
            val char by regexToken("[^-,=\\d\n]")

            val label by oneOrMore(char) map {
                it.map { it.text }.joinToString("") to it.map { it.text[0].code }.fold(0) { acc, v ->
                    ((acc + v) * 17) % 256
                }
            }
            val add by label * -eq * num map {
                val (label, value) = it
                val (labelText, hash) = label
                val focalLength = value.text.toInt()
                fun Map<Int, Map<String, Int>>.(): Map<Int, Map<String, Int>> {
                    val result = toMutableMap()
                    if (this[hash] == null) return this+(hash to mapOf(labelText to focalLength))
                    else result[hash] = this[hash]!!.toMutableMap().also { it[labelText] = focalLength }
                    return result
                }
            }
            val remove by label * -dash map {
                val (label, hash) = it
                fun Map<Int, Map<String, Int>>.(): Map<Int, Map<String, Int>> {
                    val result = toMutableMap()
                    if (result.containsKey(hash)) result[hash] = result[hash]!!.toMutableMap().apply { remove(label) }
                    return result
                }

            }

            val instr by (add or remove)


            override val root by separated(instr, comma) map {
                val result = it.fold(mapOf<Int, Map<String, Int>>()) { acc, func -> acc.func() }
                (0..255)
                        .flatMap { boxNum ->
                            (result[boxNum] ?: mapOf()).values
                                    .mapIndexed { slot, focalLength -> Triple(boxNum + 1, slot + 1, focalLength) }
                        }
                        .sumOf { (a, b, c) -> a * b * c }

            }
        }
        return parser.parseOrThrow(input)
    }

    part1("HASH").println()
    val test = "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7"
    part1(test).println()
    val input = readInputTxt("15")
    part1(input).println()
    part2(test).println()
    part2(input).println()
}