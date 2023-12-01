fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf {
            String(charArrayOf(it.first(Char::isDigit), it.last(Char::isDigit))).toInt()
        }
    }

    fun part2(input: List<String>): Int {
        val map = mapOf(
            Pair("one", "1"),
            Pair("two", "2"),
            Pair("three", "3"),
            Pair("four", "4"),
            Pair("five", "5"),
            Pair("six", "6"),
            Pair("seven", "7"),
            Pair("eight", "8"),
            Pair("nine", "9")
        )
        return input.sumOf {
            val pattern = "(${map.keys.joinToString("|")}|\\d)"
            val x1 = Regex(".*?$pattern.*").find(it)?.groupValues?.last()
            val x2 = Regex(".*$pattern.*?").find(it)?.groupValues?.last()
            val fst = map[x1] ?: x1
            val snd = map[x2] ?: x2
            (fst + snd).toInt()
        }
    }

    val input = readInput("01")
    part1(input).println()
    part2(input).println()
}
