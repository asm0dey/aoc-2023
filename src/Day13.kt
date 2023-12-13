fun main() {
    fun <T> Iterable<T>.chunkBy(predicate: (T) -> Boolean): Sequence<List<T>> {
        return sequence {
            val result = arrayListOf<T>()
            for (s in this@chunkBy) {
                if (predicate(s)) {
                    yield(result.toList())
                    result.clear()
                } else result.add(s)
            }
            if (result.isNotEmpty()) yield(result)
        }
    }

    fun List<String>.findVerticalReflection(additionalPredicate: ((String, String) -> Boolean)? = null) =
            asSequence()
                    .withIndex()
                    .windowed(2)
                    .filter { (a, b) -> a.value == b.value || (additionalPredicate != null && additionalPredicate(a.value, b.value)) }
                    .map { (a, b) -> a to b }
                    .filter { (a, b) ->
                        var usedPredicate = false
                        var i0 = a.index
                        var i1 = b.index
                        while (i0 in indices && i1 in indices) {
                            val s0 = this[i0]
                            val s1 = this[i1]
                            if (s0 == s1) {
                                i0--
                                i1++
                                continue
                            }
                            if (additionalPredicate != null && !usedPredicate && additionalPredicate(s0, s1)) {
                                usedPredicate = true
                                i0--
                                i1++
                                continue
                            }
                            return@filter false
                        }
                        true
                    }

    fun findHorizontalReflection(strings: List<String>, additionalPredicate: ((String, String) -> Boolean)? = null) =
            strings[0]
                    .indices
                    .map { idx -> strings.map { it[idx] }.joinToString("") }
                    .findVerticalReflection(additionalPredicate)


    val smallDistance = { a: String, b: String ->
        var counter = 0
        for (i in 0 until maxOf(a.length, b.length)) {
            if (i >= a.length || i >= b.length) counter++
            if (a[i] != b[i]) counter++
        }
        counter <= 1
    }

    fun part1(input: List<String>): Int = input
            .chunkBy(String::isBlank)
            .sumOf {
                val vert = it.findVerticalReflection().firstOrNull()?.first?.index?.plus(1)
                val horizontal = findHorizontalReflection(it).firstOrNull()?.first?.index?.plus(1)
                (vert ?: 0) * 100 + (horizontal ?: 0)
            }

    fun part2(input: List<String>): Int = input
            .chunkBy(String::isBlank)
            .sumOf { lines ->
                val initialVertical = lines.findVerticalReflection().firstOrNull()?.first?.index?.plus(1)
                val newVertical = lines.findVerticalReflection(smallDistance)
                        .map { it.first.index + 1 }
                        .firstOrNull { it != initialVertical }
                val initialHorizontal = findHorizontalReflection(lines).firstOrNull()?.first?.index?.plus(1)
                val newHorizon = findHorizontalReflection(lines, smallDistance)
                        .map { it.first.index + 1 }
                        .firstOrNull { it != initialHorizontal }
                (newVertical ?: 0) * 100 + (newHorizon ?: 0)
            }


    val test = readInput("13t1")
    val input = readInput("13")
    check(part1(test) == 405)
    part1(input).println()
    check(part2(test) == 400)
    part2(input).println()
}
