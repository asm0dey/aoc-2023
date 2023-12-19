import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken


fun main() {
    data class MappingEntry(val destinationStart: Long, val sourceStart: Long, val length: Long) {
        private val sourceRange: LongRange = sourceStart..(sourceStart + length)
        private val destinationRange: LongRange = destinationStart..(destinationStart + length)

        fun lookup(source: Long): Long? =
                if (source in sourceRange) destinationStart + (source - sourceStart) else null

        fun reverseLookup(destination: Long): Long? =
                if (destination in destinationRange) sourceStart + (destination - destinationStart) else null

    }

    data class Mapping(val entries: List<MappingEntry>) {
        fun lookup(source: Long): Long = entries.firstNotNullOfOrNull { it.lookup(source) } ?: source

        fun reverseLookup(destination: Long): Long =
                entries.firstNotNullOfOrNull { it.reverseLookup(destination) } ?: destination

    }

    val parser = object : Grammar<Pair<List<Long>, List<Mapping>>>(debugMode = true) {
        val num by regexToken("\\d+")
        val nl by literalToken("\n", "newline")
        val sp by regexToken(" +", "space")
        val colon by literalToken(":")
        val seedLit by literalToken("seeds")
        val toLit by literalToken("-to-")
        val mapLit by literalToken("map")
        val word by regexToken("[a-zA-Z]+")
        val range by num * -sp * num * -sp * num map {
            MappingEntry(it.t1.text.toLong(), it.t2.text.toLong(), it.t3.text.toLong())
        }
        val ranges by separated(range, nl)
        val mappingName by word * -toLit * word * -sp * -mapLit * -colon * -nl
        val mapping by -mappingName * ranges map { Mapping(it) }
        val mappings by separated(mapping, nl * nl)
        val seeds by -seedLit * -colon * -sp * separated(num, sp) * -nl * -nl map { it.map { it.text.toLong() } }

        override val root by seeds * mappings map { it.t1 to it.t2 }
    }

    fun Map<String, Pair<String, List<Pair<LongRange, LongRange>>>>.toChain(): List<List<Pair<LongRange, LongRange>>> {
        var init = keys.first()
        val list: ArrayList<List<Pair<LongRange, LongRange>>> = arrayListOf()
        while (true) {
            val tmp = this[init] ?: break
            init = tmp.first
            list.add(tmp.second)
        }
        return list.toList()
    }

    fun part1(input: Pair<List<Long>, List<Mapping>>): Long {
        val (seeds, mappings) = input
        return seeds.minOf {
            mappings.fold(it) { a, m -> m.lookup(a) }
        }
    }

    fun part2(input: Pair<List<Long>, List<Mapping>>): Long {
        val seeds = input.first.chunked(2).map { it.first()..<it.first() + it.last() }
        val mapReversed = input.second.reversed()
        return generateSequence(0L) { it + 1 }
                .first { location ->
                    val seed = mapReversed.fold(location) { acc, map -> map.reverseLookup(acc) }
                    seeds.any { seed in it }
                }
    }

    val test = parser.parseOrThrow(readInputTxt("05t1"))
    check(part1(test) == 35L)
    val input = parser.parseOrThrow(readInputTxt("05"))
    part1(input).println()
    check(part2(test) == 46L)
    part2(input).println()
}

