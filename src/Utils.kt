import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()
fun readInputTxt(name: String) = Path("src/$name.txt").readText()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
        .toString(16)
        .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun <T> dijkstraSearch(
        startingPoints: List<T>,
        neighbors: T.() -> List<T>,
        distanceBetween: (currentNode: T, nextNode: T) -> UInt,
): Map<T, UInt> {
    data class State(val node: T, val distance: UInt)

    val bestDistance = hashMapOf<T, UInt>()
    val front = PriorityQueue<State>(compareBy { it.distance })

    for (start in startingPoints) front += State(start, 0u)

    while (front.isNotEmpty()) {
        val (currentNode, currentDistance) = front.poll()
        if (currentNode in bestDistance) continue

        bestDistance[currentNode] = currentDistance

        for (nextNode in neighbors(currentNode))
            if (nextNode !in bestDistance)
                front += State(nextNode, currentDistance + distanceBetween(currentNode, nextNode))
    }

    return bestDistance
}

fun lcmFor2(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) return lcm
        lcm += larger
    }
    return maxLcm
}

fun lcm(loopLengths: Iterable<Long>): Long {
    var lcm = 1L
    for (number in loopLengths) {
        lcm = lcmFor2(lcm, number)
    }
    return lcm
}

fun lcm(vararg loopLengths: Long): Long {
    var lcm = 1L
    for (number in loopLengths) {
        lcm = lcmFor2(lcm, number)
    }
    return lcm
}
