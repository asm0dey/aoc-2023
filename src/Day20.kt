import Day20Domain.Module
import Day20Domain.Module.*
import Day20Domain.Pulse.HIGH
import Day20Domain.Pulse.LOW
import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken

class Day20Domain {
    enum class Pulse { LOW, HIGH }

    sealed interface Module {
        val name: String
        val targets: List<String>
        fun process(pulse: Pulse, input: String): List<Pair<String, Pulse>>?
        class FlipFlop(override val name: String, override val targets: List<String>) : Module {
            private var on = false
            override fun process(pulse: Pulse, input: String): List<Pair<String, Pulse>>? = when (pulse) {
                HIGH -> null
                LOW -> when (on) {
                    true -> {
                        on = false
                        targets.map { it to LOW }
                    }

                    false -> {
                        on = true
                        targets.map { it to HIGH }
                    }
                }
            }

            override fun toString() = "FlipFlop(name='$name', on=$on, targets=$targets)"

        }

        class Conjunction(override val name: String, override val targets: List<String>) : Module {
            var inputs: List<String> = listOf()
                set(value) {
                    memory = HashMap(value.associateWith { LOW })
                    field = value
                }

            private var memory: HashMap<String, Pulse> = hashMapOf()
            override fun process(pulse: Pulse, input: String): List<Pair<String, Pulse>> {
                memory[input] = pulse
                return if (memory.all { it.value == HIGH }) targets.map { it to LOW }
                else targets.map { it to HIGH }
            }

            override fun toString() = "Conjunction(name='$name', targets=$targets, memory=$memory)"

        }

        class Broadcaster(override val name: String, override val targets: List<String>) : Module {
            override fun process(pulse: Pulse, input: String): List<Pair<String, Pulse>> {
                return targets.map { it to pulse }
            }

            override fun toString() = "Broadcaster(name='$name', targets=$targets)"

        }

        class Output(override val name: String, override val targets: List<String> = listOf(), var action: (Pulse, String) -> Unit = { _, _ -> }) : Module {
            override fun process(pulse: Pulse, input: String): List<Pair<String, Pulse>>? {
                action(pulse, input)
                return null
            }

            override fun toString() = "Output(name='$name')"
        }
    }
}

fun main() {
    @Suppress("ObjectPropertyName")
    val parser = object : Grammar<List<Module>>() {
        init {
            regexToken(" +", ignored = true)
        }

        val word by regexToken("[a-z]+")
        val arrow by literalToken("->")
        val pnt by literalToken("%")
        val `&` by literalToken("&")
        val `,` by literalToken(",")
        val nl by literalToken("\n")

        val source by optional(`&` or pnt) * word
        val module by source * -arrow * separated(word, `,`) map { (sourceType, sourceName, targets) ->
            if (sourceName.text == "broadcaster") Broadcaster("broadcaster", targets.map { it.text })
            else if (sourceType == null) error("Unsupported module without type and with name $sourceName")
            else if (sourceType.text == "%") FlipFlop(sourceName.text, targets.map { it.text })
            else Conjunction(sourceName.text, targets.map { it.text })
        }
        override val root by separated(module, nl)
    }

    fun findAndEnrichModules(input: String): Map<String, Module> {
        val modules = parser.parseOrThrow(input).associateBy { it.name }
        for (con in modules.values.filterIsInstance<Conjunction>()) {
            con.inputs = modules.values.filter { it.targets.contains(con.name) }.map { it.name }
        }
        return modules
    }

    fun part1(input: String): Long {
        val modules = findAndEnrichModules(input)
        var lowCount = 0L
        var highCount = 0
        repeat(1000) {
            val actions = ArrayDeque(listOf(Triple("button", LOW, modules["broadcaster"]!!)))
            while (actions.isNotEmpty()) {
                val (source, pulse, target) = actions.removeFirst()
                if (pulse == LOW) lowCount++ else highCount++
                val res = target.process(pulse, source) ?: continue
                for ((newTarget, newPulse) in res)
                    actions.add(Triple(target.name, newPulse, modules[newTarget] ?: Output(newTarget)))
            }
        }
        return lowCount * highCount
    }


    fun part2(input: String): Long {
        val modules = findAndEnrichModules(input)
        val rxSource = modules.values.single { "rx" in it.targets }.name
        val interesting = modules.values.filter { rxSource in it.targets }.map { it.name }
        val occurrences = interesting.associateWith { arrayListOf<Long>() }
        var presses = 0L
        out@ while (true) {
            val actions = ArrayDeque(listOf(Triple("button", LOW, modules["broadcaster"]!!)))
            while (actions.isNotEmpty()) {
                val (source, pulse, target) = actions.removeFirst()
                if (target.name == "rx" && pulse == LOW) break@out
                if (pulse == HIGH && source in interesting) {
                    occurrences[source]!!.add(presses)
                    if (occurrences.values.all { it.size >= 2 })
                        return lcm(occurrences.values.map { (a, b) -> b - a })
                }
                val res = target.process(pulse, source) ?: continue
                for ((newTarget, newPulse) in res)
                    actions.add(Triple(target.name, newPulse, modules[newTarget] ?: Output(newTarget)))
            }
            presses++
        }
        return -1L
    }

    val test1 = readInputTxt("20t1")
    val test2 = readInputTxt("20t2")
    part1(test1)
    part1(test2)
    val input = readInputTxt("20")
    part1(input).println()
    part2(input).println()
}