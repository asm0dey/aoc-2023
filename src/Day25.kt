import com.google.common.collect.ImmutableSet
import com.google.common.graph.GraphBuilder
import com.google.common.graph.ImmutableGraph
import kotlin.math.min

fun main() {

    val lines = readInput("25").map { it.split(*"->: ,;".toCharArray()).filter { it.isNotBlank() } }
        .map { it.first() to it.drop(1) }
    val names = lines.map { it.second + it.first }.flatten().distinct()
    val x = GraphBuilder.undirected().allowsSelfLoops(true).build<String>()
    for ((source, targets) in lines) {
        for (target in targets) {
            x.putEdge(source, target)
        }
    }
    val y = ImmutableGraph.copyOf(x)
    val visited = hashSetOf(names.first())
    val toVisit = ArrayDeque(y.adjacentNodes(visited.first()))
    while (toVisit.isNotEmpty()) {
        val removeFirst = toVisit.removeFirst()
        visited.add(removeFirst)
        toVisit.addAll(y.adjacentNodes(removeFirst).filterNot { it in visited })
    }
    println((visited.size-1) * (names.size - visited.size))
}