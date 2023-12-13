fun main() {
    fun <T> Iterable<T>.chunkBy(predicate: (T) -> Boolean): Sequence<List<T>> = sequence {
        val result = fold(emptyList<T>()) { acc, element ->
            if (predicate(element)) {
                yield(acc)
                emptyList()
            } else {
                acc + element
            }
        }
        if (result.isNotEmpty()) yield(result)
    }

    tailrec fun List<String>.checkSymmetry(i0: Int, i1: Int, additionalPredicate: ((String, String) -> Boolean)?): Boolean =
            when {
                i0 in indices && i1 in indices -> when {
                    this[i0] == this[i1] -> checkSymmetry(i0 - 1, i1 + 1, additionalPredicate)
                    additionalPredicate != null && additionalPredicate(this[i0], this[i1]) ->
                        checkSymmetry(i0 - 1, i1 + 1, null)

                    else -> false
                }

                else -> true
            }


    fun List<String>.findVerticalReflection(additionalPredicate: ((String, String) -> Boolean)? = null) =
            asSequence()
                    .withIndex()
                    .windowed(2)
                    .filter { (a, b) -> a.value == b.value || (additionalPredicate != null && additionalPredicate(a.value, b.value)) }
                    .map { (a, b) -> a to b }
                    .filter { (a, b) -> checkSymmetry(a.index, b.index, additionalPredicate) }


    fun List<String>.findHorizontalReflection(additionalPredicate: ((String, String) -> Boolean)? = null) =
            this[0]
                    .indices
                    .map { idx -> map { it[idx] }.joinToString("") }
                    .findVerticalReflection(additionalPredicate)


    fun smallDistance(a: String, b: String): Boolean {
        val maxLength = maxOf(a.length, b.length)

        val counter = (0 until maxLength).count { it >= a.length || it >= b.length || a[it] != b[it] }

        return counter <= 1
    }

    fun part1(input: List<String>): Int = input
            .chunkBy(String::isBlank)
            .sumOf {
                val vert = it.findVerticalReflection().firstOrNull()?.first?.index?.plus(1)
                val horizontal = it.findHorizontalReflection().firstOrNull()?.first?.index?.plus(1)
                (vert ?: 0) * 100 + (horizontal ?: 0)
            }

    fun part2(input: List<String>): Int = input
            .chunkBy(String::isBlank)
            .sumOf { lines ->
                val initialVertical = lines.findVerticalReflection().firstOrNull()?.first?.index?.plus(1)
                val newVertical = lines.findVerticalReflection(::smallDistance)
                        .map { it.first.index + 1 }
                        .firstOrNull { it != initialVertical }
                val initialHorizontal = lines.findHorizontalReflection().firstOrNull()?.first?.index?.plus(1)
                val newHorizon = lines.findHorizontalReflection(::smallDistance)
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
