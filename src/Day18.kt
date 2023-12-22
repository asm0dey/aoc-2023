import kotlin.math.absoluteValue

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    data class DigCommand(val direction: Point, val amount: Int)

    fun Point.neighbors() = listOf(LEFT, RIGHT, UP, DOWN).map { this + it }

    fun part1(input: String) {
        val commands = input.lines()
                .map {
                    val (d, n) = it.split(' ', '#', '(', ')')
                    DigCommand(when (d) {
                        "L" -> LEFT
                        "R" -> RIGHT
                        "U" -> UP
                        "D" -> DOWN
                        else -> error("WFT is $d")
                    }, n.toInt())
                }
        var curPos = Point(0,0)
        val dugOut = hashSetOf(curPos)
        for (command in commands) {
            repeat(command.amount) {
                val newPos = curPos + command.direction
                dugOut += newPos
                curPos = newPos
            }
        }
        val toVisit = ArrayDeque(listOf(Point(1,1)))
        val found = hashSetOf<Point>()
        while (toVisit.isNotEmpty()) {
            val next = toVisit.removeFirst()
            if (next in found || next in dugOut) continue
            found += next
            val neighbors = next.neighbors()
            neighbors
                    .filterNot(found::contains)
                    .filterNot(dugOut::contains)
                    .forEach(toVisit::add)
        }
        println(found.size + dugOut.size)
    }

    fun part2(input: String): Long {
        val commands = input.lines()
                .map {
                    val (_, _, _, _, c) = it.split(' ', '#', '(', ')')
                    val dir = when (c.last()) {
                        '2' -> LEFT
                        '0' -> RIGHT
                        '3' -> UP
                        '1' -> DOWN
                        else -> error("WFT is ${c.last()}")
                    }
                    val amount = c.substring(0..4).hexToInt()
                    DigCommand(dir, amount)
                }
        val angles = arrayListOf(Point(0,0))
        var perimeter = 0L
        for (command in commands) {
            perimeter += command.amount
            angles += angles.last() + command.direction * command.amount
        }
        // Shoelace formula + half of perimeter (shoelace already counts another half) + 1
        return perimeter / 2 + 1 + angles
                .windowed(2)
                .sumOf { (a, b) ->
                    val (x0, y0) = a
                    val (x1, y1) = b
                    x0.toLong() * y1 - y0.toLong() * x1
                }
                .absoluteValue / 2
    }

    val test = readInputTxt("18t1")
    part1(test)
    val input = readInputTxt("18")
    part1(input)
    part2(test).println()
    part2(input).println()

}