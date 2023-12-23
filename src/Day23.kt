import java.util.BitSet

data class Edge(val start: BitSet, val end: BitSet, val distance: Int)

fun main() {
    val exits = mapOf(
        '<' to listOf(LEFT),
        '>' to listOf(RIGHT),
        '^' to listOf(UP),
        'v' to listOf(DOWN),
        '.' to listOf(UP, DOWN, LEFT, RIGHT),
        '#' to listOf()
    )

    fun removeSlopes(input: List<String>): List<String> =
        input.map {
            it.replace(Regex("[<>v^]"), ".")
        }


    fun Map<Point, Char>.isFree(p: Point) = p in this && this[p] != '#'
    fun Map<Point, Char>.isRoad(p: Point) = exits['.']!!.count { isFree(p + it) } == 2
    fun Map<Point, Char>.distance(crossroadA: Point, crossroadB: Point): Int {
        val path = ArrayDeque(listOf(Pair(crossroadA, 0)))

        val visited = hashSetOf(crossroadA)
        while (path.isNotEmpty()) {
            val (pos, dist) = path.removeFirst()
            for (dir in exits[this[pos]] ?: listOf()) {
                val posT = pos + dir
                if (posT == crossroadB) {
                    return dist + 1
                } else if (isRoad(posT) && !visited.contains(posT)) {
                    visited.add(posT)
                    path.add(Pair(posT, dist + 1))
                }
            }
        }
        return -1
    }


    fun List<String>.parseMap(): Map<Point, Char> {
        return indices
            .flatMap { y ->
                this[0].indices.map { x ->
                    Pair(Point(x to y), this[y][x])
                }
            }
            .toMap()
    }

    fun node(idx: Int): BitSet {
        return BitSet().apply { set(idx) }
    }

    fun List<String>.makeGraph(): Pair<List<BitSet>, ArrayList<Edge>> {
        val map = parseMap()
        val nodePositions = map.keys
            .filter { map.isFree(it) && !map.isRoad(it) }
            .sortedWith(compareBy({ it.y }, { it.x }))


        val nodes = nodePositions.indices.map { node(it) }
        val edges = arrayListOf<Edge>()
        for (i in nodePositions.indices) {
            for (j in nodePositions.indices) {
                if (i != j) {
                    val d = map.distance(nodePositions[i], nodePositions[j])
                    if (d > 0) edges += Edge(nodes[i], nodes[j], d)
                }
            }
        }
        return Pair(nodes, edges)
    }

    fun List<String>.solve(): Int {
        val (nodes, edges) = makeGraph()
        val (start, goal) = Pair(nodes.first(), nodes.last())

        val cache = HashMap<Pair<BitSet, BitSet>, Int>()
        fun clone(visited: BitSet) = visited.clone() as BitSet

        fun longestPath(node: BitSet, visited: BitSet): Int {
            if (node == goal) return 0
            else if (clone(visited).apply { and(node) }.cardinality() != 0) return Int.MIN_VALUE
            val key = Pair(node, visited)
            if (!cache.containsKey(key)) {
                cache[key] = edges
                    .filter { it.start == node }
                    .maxOfOrNull { it.distance + longestPath(it.end, clone(visited).apply { or(node) }) }
                    ?: 0
            }
            return cache[key] ?: 0
        }
        return longestPath(start, BitSet())
    }

    fun partOne(input: List<String>): Any = input.solve()
    fun partTwo(input: List<String>): Any = input.solve()


    val input = readInput("23")
    println(partOne(input))
    println(partTwo(removeSlopes(input)))
}
