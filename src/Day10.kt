fun main() {

    fun neighbors(current: Pair<Int, Int>) = setOf(current.copy(second = current.second - 1),
            current.copy(second = current.second + 1),
            current.copy(first = current.first - 1),
            current.copy(first = current.first + 1))

    fun indicesByLetter(letter: Char, current: Pair<Int, Int>): Set<Pair<Int, Int>> {
        return when (letter) {
            'F' -> setOf(current.copy(second = current.second + 1), current.copy(first = current.first + 1))
            'L' -> setOf(current.copy(second = current.second - 1), current.copy(first = current.first + 1))
            'J' -> setOf(current.copy(second = current.second - 1), current.copy(first = current.first - 1))
            '7' -> setOf(current.copy(second = current.second + 1), current.copy(first = current.first - 1))
            '.' -> setOf()
            '-' -> setOf(current.copy(first = current.first - 1), current.copy(first = current.first + 1))
            '|' -> setOf(current.copy(second = current.second - 1), current.copy(second = current.second + 1))
            'S' -> neighbors(current)

            else -> error("Unknown input $letter at $current")
        }
    }

    fun findLoop(readInput: List<String>): HashSet<Pair<Int, Int>> {
        val map = hashMapOf<Pair<Int, Int>, Set<Pair<Int, Int>>>()
        for ((y, line) in readInput.withIndex()) {
            for ((x, letter) in line.withIndex()) {
                map[x to y] = indicesByLetter(letter, x to y)
            }
        }
        val start = map.entries.find { it.value.size == 4 }!!.key
        val visited = hashSetOf(start)
        var tails = map.entries.filter { it.value.contains(start) }.map { it.key }
        visited.addAll(tails)
        while (true) {
            tails = tails.flatMap { map[it]!! }.filterNot { visited.contains(it) }
            if (tails.isEmpty()) break
            visited.addAll(tails)
        }
        return visited
    }


    fun Set<Pair<Int, Int>>.findEmptyTiles(): Set<Pair<Int, Int>> {
        val top = minOf { it.second }
        val bottom = maxOf { it.second }
        val left = minOf { it.first }
        val right = maxOf { it.first }
        val res = hashSetOf<Pair<Int, Int>>()
        for (y in top..bottom) {
            for (x in left..right) {
                val coord = x to y
                if (!contains(coord) && x % 2 == 0 && y % 2 == 0)
                    res.add(coord)
            }
        }
        return res
    }

    fun Pair<Int, Int>.isOuter(loop: MutableSet<Pair<Int, Int>>): Pair<Boolean, Set<Pair<Int, Int>>> {
        val toVisit = ArrayDeque(neighbors(this))
        val visited = hashSetOf(this)
        val maxRight = loop.maxOf { it.first } + 1
        val maxDown = loop.maxOf { it.second } + 1
        val verticalBorders = arrayOf(-1, maxDown)
        val horizontalBorders = arrayOf(-1, maxRight)
        val alsoEmpty = hashSetOf(this)
        while (toVisit.isNotEmpty()) {
            val next = toVisit.removeFirst()
            if (next.first in horizontalBorders || next.second in verticalBorders)
                return true to alsoEmpty
            if (visited.contains(next)) {
                alsoEmpty += next
                continue
            }
            if (loop.contains(next)) {
                visited.add(next)
                continue
            }
            visited.add(next)
            toVisit += neighbors(next)
        }
        return false to alsoEmpty

    }

    fun MutableSet<Pair<Int, Int>>.removeOuter(loop: MutableSet<Pair<Int, Int>>): Set<Pair<Int, Int>> {
        val tmp = toSet()
        val internal = hashSetOf<Pair<Int, Int>>()
        while (isNotEmpty()) {
            val first = first()
            val outer = first.isOuter(loop)
            remove(first)
            removeAll(outer.second.intersect(tmp))
            if (!outer.first) {
                internal.add(first)
                internal.addAll(outer.second.intersect(tmp))
            }
        }
        return internal
    }

    fun part1(readInput: List<String>): Int {
        val visited = findLoop(readInput)
        return visited.size / 2
    }

    fun part2(test: List<String>): Int {
        val loop = findLoop(test)
        val map = hashSetOf<Pair<Int, Int>>()
        for ((y, line) in test.withIndex()) {
            for ((x, c) in line.withIndex()) {
                if (loop.contains(x to y)) {
                    map.add(x * 2 to y * 2)
                }
                if (loop.contains(x to y) &&
                        loop.contains(x + 1 to y) &&
                        c in "F-LS" &&
                        test[y][x + 1] in "-7JS") {
                    map.add(x * 2 + 1 to y * 2)
                }
                if (loop.contains(x to y) &&
                        loop.contains(x to y + 1) &&
                        c in "F7|S" &&
                        test[y + 1][x] in "|JLS") {
                    map.add(x * 2 to y * 2 + 1)
                }
            }
        }
        return map.findEmptyTiles().toHashSet().removeOuter(map).size
    }

    val test1 = readInput("10t1")
    val test2 = readInput("10t2")
    val test3 = readInput("10t3")
    val test4 = readInput("10t4")
    check(part1(test1) == 8)
    val input = readInput("10")
    part1(input).println()
    check(part2(test2) == 10)
    check(part2(test3) == 4)
    check(part2(test4) == 8)
    part2(input).println()
}




