import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import com.google.common.graph.ImmutableGraph

@Suppress("UnstableApiUsage")
fun main() {

    val lines = readInput("25").map { it.split(*"->: ,;".toCharArray()).filter { it.isNotBlank() } }
        .map { it.first() to it.drop(1) }
    val names = lines.map { it.second + it.first }.flatten().distinct()
    val x = GraphBuilder.undirected()
        .immutable<String>()
        .apply {
            lines
                .flatMap { (source, targets) ->
                    targets.map {
                        source to it
                    }
                }
                .forEach { (a, b) -> putEdge(a, b) }

        }
        .build()
    val set1 = Graphs.reachableNodes(x, names.first())
    val set2 = Graphs.reachableNodes(x, names.first { it !in set1 })
    println("$set1 $set2")
    println(set1.size * set2.size)
}