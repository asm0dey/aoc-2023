import arrow.core.curried

fun main() {

    fun moveNorth(map: Collection<Pair<Int, Int>>, x: Int, y: Int, stableRocks: Set<Pair<Int, Int>>): Pair<Int, Int> {
        val boulder = (0 until y).lastOrNull { stableRocks.contains(x to it) } ?: 0
        return (x to y - (y downTo boulder).count { x to it !in map && x to it !in stableRocks }).also { println("$x $y [${it.first} ${it.second}]") }
    }

    fun moveSouth(map: Collection<Pair<Int, Int>>, x: Int, y: Int, stableRocks: Set<Pair<Int, Int>>, lastIdx: Int): Pair<Int, Int> {
        val boulder = (y..lastIdx).firstOrNull { stableRocks.contains(x to it) } ?: lastIdx
        return x to y + (y..boulder).count { x to it !in map && x to it !in stableRocks }
    }

    fun moveWest(map: Collection<Pair<Int, Int>>, x: Int, y: Int, stableRocks: Set<Pair<Int, Int>>): Pair<Int, Int> {
        val boulder = (0 until x).lastOrNull { stableRocks.contains(it to y) } ?: 0
        return (x - (x downTo boulder).count { it to y !in map && it to y !in stableRocks }) to y
    }

    fun moveEast(map: Collection<Pair<Int, Int>>, x: Int, y: Int, stableRocks: Set<Pair<Int, Int>>, lastIdx: Int): Pair<Int, Int> {
        val boulder = (x..lastIdx).firstOrNull { stableRocks.contains(it to y) } ?: lastIdx
        return (x + (x..boulder).count { it to y !in map && it to y !in stableRocks }) to y
    }

    fun moveMapNorth(stableRocks: Set<Pair<Int, Int>>, unstableRocks: Collection<Pair<Int, Int>>): Set<Pair<Int, Int>> {
        return unstableRocks
                .map { (x, y) -> moveNorth(unstableRocks, x, y, stableRocks) }
                .toSet()
    }

    fun moveMapWest(stableRocks: Set<Pair<Int, Int>>, unstableRocks: Collection<Pair<Int, Int>>): Set<Pair<Int, Int>> {
        return unstableRocks
                .map { (x, y) -> moveWest(unstableRocks, x, y, stableRocks) }
                .toSet()
    }

    fun moveMapSouth(stableRocks: Set<Pair<Int, Int>>, lastIdx: Int, unstableRocks: Collection<Pair<Int, Int>>): Set<Pair<Int, Int>> {
        return unstableRocks
                .map { (x, y) -> moveSouth(unstableRocks, x, y, stableRocks, lastIdx) }
                .toSet()
    }

    fun moveMapEast(stableRocks: Set<Pair<Int, Int>>, lastIdx: Int, unstableRocks: Collection<Pair<Int, Int>>): Set<Pair<Int, Int>> {
        return unstableRocks
                .map { (x, y) -> moveEast(unstableRocks, x, y, stableRocks, lastIdx) }
                .toSet()
    }

    fun load(unstableRocks: Collection<Pair<Int, Int>>, input: List<String>) =
            unstableRocks.sumOf { input.size - it.second }


    fun <T> findRepeatingPattern(elements: List<T>): Pair<Int, Int>? {
        tailrec fun findPattern(i: Int): Pair<Int, Int>? {
            if (i >= elements.size - 1) return null

            val j = (i + 1 until elements.size).firstOrNull { elements[i] == elements[it] }

            return when {
                j != null -> i to (j - i)
                else -> findPattern(i + 1)
            }
        }

        return findPattern(0)
    }


    fun stableAndUnstable(input: List<String>): Pair<Set<Pair<Int, Int>>, Set<Pair<Int, Int>>> {
        val map = input
                .flatMapIndexed { y, line ->
                    line.mapIndexed { x, c ->
                        (x to y) to c
                    }
                }
                .filter { it.second in arrayOf('#', 'O') }
                .groupBy { it.second }
                .mapValues { it.value.map { it.first } }
        return Pair(map['#']!!.toSet(), map['O']!!.toSet())
    }

    fun part1(input: List<String>): Int {
        val (stableRocks, unstableRocks) = stableAndUnstable(input)
        return load(moveMapNorth(stableRocks, unstableRocks), input)
    }

    fun <T, R> ((T) -> R).asReceiver(): T.() -> R = this

    fun part2(input: List<String>): Int {
        val (stableRocks, unstableRocks) = stableAndUnstable(input)
        val curNorth = ::moveMapNorth.curried()(stableRocks).asReceiver()
        val curWest = ::moveMapWest.curried()(stableRocks).asReceiver()
        val curSouth = ::moveMapSouth.curried()(stableRocks)(input.size - 1).asReceiver()
        val curEast = ::moveMapEast.curried()(stableRocks)(input[0].length - 1).asReceiver()
        val iterations = arrayListOf(unstableRocks)
        while (true) {
            iterations += iterations.last().curNorth().curWest().curSouth().curEast()
            val seq = findRepeatingPattern(iterations)
            if (seq != null) {
                println("Pattern found! Starts at ${seq.first}, length is ${seq.second}")
                val lastIterationIndex = seq.first + (1000000000 - seq.first) % seq.second
                return load(iterations[lastIterationIndex], input)
            }
        }
    }

    val test = readInput("14t1")
    check(part1(test) == 136)
    val input = readInput("14")
    part1(input).println()
    check(part2(test) == 64)
    part2(input).println()
}