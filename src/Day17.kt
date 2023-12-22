import arrow.core.partially2
import java.util.*


fun main() {
    data class State(val position: Point, val dir: Point, val sameDirMoves: Int)

    fun List<List<Int>>.neighbors(state: State, minMoves: Int, maxMoves: Int): List<State> = buildList {
        for (dir in listOf(UP, DOWN, LEFT, RIGHT)) {
            val newPosition = state.position + dir
            val movesSoFar = if (dir == state.dir) state.sameDirMoves + 1 else 1

            val (a, b) = newPosition
            if (a !in this@neighbors.indices || b !in this@neighbors[0].indices) continue
            if (movesSoFar > maxMoves) continue
            if (dir.x * -1 == state.dir.x && dir.y * -1 == state.dir.y) continue
            if (dir != state.dir && state.sameDirMoves < minMoves) continue

            add(State(newPosition, dir, movesSoFar))
        }
    }

    fun startingPoint(dir: Point): State = State(Point(0, 0), dir, 0)

    fun solve(s: List<String>, minMoves: Int = 1, maxMoves: Int = 3): UInt {
        val grid = s.map { it.map { char -> char.toString().toInt() } }

        val startingPoints = listOf(UP, DOWN, LEFT, RIGHT).map { startingPoint(it) }
        val neighbors = grid::neighbors.partially2(minMoves).partially2(maxMoves)

        val bestDistance = dijkstraSearch(startingPoints, neighbors) { _, nextNode ->
            grid[nextNode.position.x][nextNode.position.y].toUInt()
        }
        val result = bestDistance.filter { (k, _) -> k.position == Point(grid.indices.last, grid[0].indices.last) }
        result.forEach { (k, v) -> println("$k $v") }
        return result.minOf { (_, value) -> value }
    }

    val test = readInput("17t1")
    val input = readInput("17")
    println(solve(test))
    println(solve(input))
    println(solve(test, 4, 10))
    println(solve(input, 4, 10))
}

fun Pair<Int, Int>.toPoint(): Point = Point(first, second)
