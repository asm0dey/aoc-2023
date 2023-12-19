import Day19Workflow.*
import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken

private sealed interface Day19Workflow {
    val name: String

    data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
        fun count() = x + m + a + s
    }

    data class RangePart(val x: IntRange, val m: IntRange, val a: IntRange, val s: IntRange) {
        fun update(field: String, range: IntRange) = when (field) {
            "x" -> copy(x = range)
            "m" -> copy(m = range)
            "a" -> copy(a = range)
            "s" -> copy(s = range)
            else -> error("WTF is $field")
        }

        fun count() = listOf(x, m, a, s).map { it.last.toLong() - it.first + 1 }.reduce(Long::times)

    }

    data object Accepted : Day19Workflow {
        override val name: String = "A"
    }

    data object Rejected : Day19Workflow {
        override val name: String = "R"
    }

    data class RuleWorkflow(override val name: String, val blocks: List<(Part) -> String?>) : Day19Workflow
    data class RangeRuleWorkflow(override val name: String, val blocks: List<RangePart.() -> List<Pair<RangePart, String?>>>) : Day19Workflow {
        override fun toString(): String = name
    }

}

fun main() {
    abstract class Day19Grammar : Grammar<Pair<List<Day19Workflow>, List<Part>>>() {
        val `{` by literalToken("{")
        val `}` by literalToken("}")
        val less by literalToken("<")
        val more by literalToken(">")
        val `=` by literalToken("=")
        val colon by literalToken(":")
        val `,` by literalToken(",")
        val nl by literalToken("\n", ignored = true)
        val num by regexToken("\\d+")
        val word by regexToken("[a-zA-Z]+")
        val parameter = word * -`=` * num
        val part = -`{` * separated(parameter, `,`) * -`}` map { (xp, mp, ap, sp) ->
            Part(xp.second.text.toInt(), mp.second.text.toInt(), ap.second.text.toInt(), sp.second.text.toInt())
        }
        val parts by separated(part, nl)
        abstract val workflows: Parser<List<Day19Workflow>>

        override val root: Parser<Pair<List<Day19Workflow>, List<Part>>>
            get() = workflows * parts map { it.toPair() }
    }

    val parser1 = object : Day19Grammar() {

        val rule by word * (less or more) * num * -colon * word map { (a, b, c, d) ->
            fun(p: Part): String? {
                val data = when (a.text) {
                    "x" -> p.x
                    "m" -> p.m
                    "a" -> p.a
                    "s" -> p.s
                    else -> error("WTF is ${a.text}")
                }
                val expected = c.text.toInt()

                val comparison = when (b.text) {
                    ">" -> data > expected
                    "<" -> data < expected
                    else -> error("WTF is ${b.text}")
                }
                return if (comparison) d.text else null
            }
        }
        val workflow by word * -`{` * separated(rule, `,`, trailingSeparator = true) * word * -`}` map { (a, b, c) ->
            RuleWorkflow(a.text, b + { _: Part -> c.text })
        }
        override val workflows by separated(workflow, nl, trailingSeparator = true)

    }

    val parser2 = object : Day19Grammar() {

        val rule by word * (less or more) * num * -colon * word map {
            fun RangePart.(): List<Pair<RangePart, String?>> {
                val field = it.t1.text
                val data = when (field) {
                    "x" -> x
                    "m" -> m
                    "a" -> a
                    "s" -> s
                    else -> error("WTF is $field")
                }
                val expected = it.t3.text.toInt()

                return when (it.t2.text) {
                    ">" -> when {
                        data.first > expected -> listOf(this to it.t4.text)
                        data.last <= expected -> listOf(this to null)
                        else -> listOf(
                                update(field, data.first..expected) to null,
                                update(field, expected + 1..data.last) to it.t4.text
                        )
                    }

                    "<" -> when {
                        data.last < expected -> listOf(this to it.t4.text)
                        data.first >= expected -> listOf(this to null)
                        else -> listOf(
                                update(field, data.first until expected) to it.t4.text,
                                update(field, expected..data.last) to null
                        )
                    }

                    else -> error("WTF is ${it.t2.text}")
                }
            }
        }

        val workflow by word * -`{` * separated(rule, `,`, trailingSeparator = true) * word * -`}` map { (a, b, c) ->
            RangeRuleWorkflow(a.text, b + { listOf(this to c.text) })
        }
        override val workflows by separated(workflow, nl, trailingSeparator = true)
    }

    fun part1(input: String): Int {
        val (workflows, parts) = parser1.parseOrThrow(input)
        val map = (workflows + Accepted + Rejected).associateBy { it.name }
        val inputWorkflow = map["in"]!!
        val accepted = hashSetOf<Part>()
        val rejected = hashSetOf<Part>()
        for (part in parts) {
            var curWorkflow = inputWorkflow
            while (true) {
                when (curWorkflow) {
                    Accepted -> {
                        accepted += part
                        break
                    }

                    Rejected -> {
                        rejected += part
                        break
                    }

                    is RuleWorkflow -> {
                        for (block in (curWorkflow as RuleWorkflow).blocks) {
                            val result = block(part)
                            if (result == null) continue
                            else {
                                curWorkflow = map[result]!!
                                break
                            }
                        }
                    }

                    else -> {}
                }

            }
        }
        return accepted.sumOf(Part::count)
    }

    fun part2(input: String): Long {
        val (workflows, _) = parser2.parseOrThrow(input)
        val map = (workflows + Accepted + Rejected).associateBy { it.name }
        val inputWorkflow = map["in"]!!
        val accepted = hashSetOf<RangePart>()
        val rejected = hashSetOf<RangePart>()
        val review = ArrayDeque(listOf(RangePart(1..4000, 1..4000, 1..4000, 1..4000) to inputWorkflow))
while (review.isNotEmpty()) {
    val (part, curWorkflow) = review.removeFirst()
    when (curWorkflow) {
        Accepted -> accepted += part
        Rejected -> rejected += part
        is RangeRuleWorkflow -> {
            val localReview = ArrayDeque(listOf(part))
            while (localReview.isNotEmpty()) {
                for (block in curWorkflow.blocks) {
                    val nextP = localReview.removeFirst()
                    val list = nextP.block()
                    for ((nextPart, nextFlow) in list) {
                        if (nextFlow != null)
                            review.add(nextPart to map[nextFlow]!!)
                        else
                            localReview.add(nextPart)
                    }
                }
            }
        }

        else -> error("Unsupported workflow: $curWorkflow")
    }

}

        return accepted.sumOf(RangePart::count)
    }


    val test = readInputTxt("19t1")
    val input = readInputTxt("19")
    check(part1(test) == 19114)
    part1(input).println()
    check(part2(test) == 167409079868000L)
    part2(input).println()
}

