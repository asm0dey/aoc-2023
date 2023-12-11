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
        visited += tails
        while (true) {
            tails = tails.flatMap { map[it]!! }.filterNot { visited.contains(it) }
            if (tails.isEmpty()) break
            visited += tails
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
                    res += coord
            }
        }
        return res
    }

    fun Pair<Int, Int>.isOuter(magnifiedLoop: MutableSet<Pair<Int, Int>>): Pair<Boolean, Set<Pair<Int, Int>>> {
        val toVisit = ArrayDeque(neighbors(this))
        val visited = hashSetOf(this)
        val maxRight = magnifiedLoop.maxOf { it.first } + 1
        val maxDown = magnifiedLoop.maxOf { it.second } + 1
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
            if (magnifiedLoop.contains(next)) {
                visited += next
                continue
            }
            visited += next
            toVisit += neighbors(next)
        }
        return false to alsoEmpty

    }

    fun MutableSet<Pair<Int, Int>>.removeOuter(magnifiedLoop: MutableSet<Pair<Int, Int>>): Set<Pair<Int, Int>> {
        val tmp = toSet()
        val internal = hashSetOf<Pair<Int, Int>>()
        while (isNotEmpty()) {
            val first = first()
            val (outer, group) = first.isOuter(magnifiedLoop)
            remove(first)
            removeAll(group.intersect(tmp))
            if (!outer) {
                internal += first
                internal += group.intersect(tmp)
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
        val magnifiedLoop = hashSetOf<Pair<Int, Int>>()
        for ((y, line) in test.withIndex()) {
            for ((x, c) in line.withIndex()) {
                val element = x to y
                if (loop.contains(element))
                    magnifiedLoop += x * 2 to y * 2
                if (loop.contains(element) && loop.contains(x + 1 to y) && c in "F-LS" && test[y][x + 1] in "-7JS")
                    magnifiedLoop += x * 2 + 1 to y * 2;
                if (loop.contains(element) && loop.contains(x to y + 1) && c in "F7|S" && test[y + 1][x] in "|JLS")
                    magnifiedLoop += x * 2 to y * 2 + 1
            }
        }
        return magnifiedLoop.findEmptyTiles().toHashSet().removeOuter(magnifiedLoop).size
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




