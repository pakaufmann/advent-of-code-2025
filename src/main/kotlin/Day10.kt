import java.io.File
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.absoluteValue
import kotlin.math.min
import  kotlin.math.max
import com.microsoft.z3.Context
import com.microsoft.z3.Status

fun main(args: Array<String>) {
    data class MachineState1(val lightsOn: Int, val count: Int)

    fun part1(machines: List<Machine>): Int =
        machines.sumOf { machine ->
            val queue = mutableListOf(MachineState1(0, 0))
            val found = mutableSetOf<Int>()

            while (queue.isNotEmpty()) {
                val next = queue.removeFirst()
                if (found.contains(next.lightsOn)) {
                    continue
                } else {
                    found.add(next.lightsOn)
                }

                for (button in machine.buttons) {
                    val on = next.lightsOn xor button.toggles

                    if (on == machine.lightsOn) {
                        return@sumOf next.count + 1
                    } else {
                        queue.add(MachineState1(on, next.count + 1))
                    }
                }
            }
            0
        }

    data class MachineState2(val joltages: List<Int>, val count: Int, val usedButtons: Set<Button>)

    fun part2Z3(machines: List<Machine>): Int {
        return machines.sumOf { machine ->
            val target = machine.joltages
            val buttons = machine.buttons.map { it.toggleList }
            Context().use { ctx ->
                val opt = ctx.mkOptimize()
                val vars = buttons.indices.map { ctx.mkIntConst("b$it") }
                vars.forEach { opt.Add(ctx.mkGe(it, ctx.mkInt(0))) }
                target.indices.forEach { i ->
                    val terms = buttons.withIndex().filter { i in it.value }.map { vars[it.index] }
                    if (terms.isNotEmpty()) {
                        val sum = if (terms.size == 1) terms[0]
                        else ctx.mkAdd(*terms.toTypedArray())
                        opt.Add(ctx.mkEq(sum, ctx.mkInt(target[i])))
                    } else if (target[i] != 0) throw RuntimeException("")
                }
                opt.MkMinimize(ctx.mkAdd(*vars.toTypedArray()))
                if (opt.Check() == Status.SATISFIABLE) {
                    vars.sumOf { opt.model.evaluate(it, false).toString().toInt() }
                } else 0
            }
        }
    }

    // BFS solution with pruning
    // Note: Does not work on all inputs as some are simply too complex to calculate.
    fun part2(machines: List<Machine>): Int =
        machines.sumOf { machine ->
            println(machine)
            val queue = PriorityQueue<MachineState2>(compareBy { it.joltages.sum() })
            queue.add(MachineState2(machine.joltages, 0, setOf()))

            val seen = mutableMapOf<List<Int>, Int>()
            var lowestCnt = Int.MAX_VALUE
            var cnt = 0

            val joltageToButtons = mutableMapOf<Int, Set<Button>>()
            for (button in machine.buttons) {
                for ((i, _) in machine.joltages.withIndex()) {
                    if (button.toggleList.contains(i)) {
                        joltageToButtons.compute(i) { _, set ->
                            if (set != null) {
                                set + button
                            } else {
                                setOf(button)
                            }
                        }
                    }
                }
            }

            while (queue.isNotEmpty()) {
                val next = queue.poll()
                if (seen.getOrDefault(next.joltages, Int.MAX_VALUE) < next.count) {
                    continue
                } else {
                    seen[next.joltages] = next.count
                }
                val below = next.joltages.any { it < 0 }
                if (below) {
                    continue
                }

                val highest = next.joltages.maxOf { it }

                if (next.count + highest >= lowestCnt) {
                    continue
                }

                val (nullIndices, nonNullIndices) = next.joltages
                    .withIndex()
                    .partition { it.value == 0 }

                val remainingButtons = machine.buttons.filter { !next.usedButtons.contains(it) }

                val possible = nonNullIndices.all { nonNull ->
                    remainingButtons.any {
                        it.toggleList.contains(nonNull.index)
                    }
                }

                if (!possible) {
                    continue
                }

                cnt++
                if (cnt % 100000 == 0) {
                    println(cnt)
                    println(lowestCnt)
                    println(next)
                    println(next.joltages.sum())
                    println(queue.take(10).map { it.joltages.sum() })
                }

                /*val lowest = next.joltages
                    .withIndex()
                    .filter { it.value != 0 }
                    .minByOrNull { it.value }!!*/

                /*val toClick = remainingButtons.filter {
                    it.toggles and (1 shl lowest.index) == (1 shl lowest.index) &&
                            nullIndices.none { ni -> it.toggles and (1 shl ni.index) == (1 shl ni.index) }
                }*/
                val (joltageIndex, toClick) = joltageToButtons
                    .mapValues { it.value - next.usedButtons }
                    .filter { it.value.isNotEmpty() }
                    .minByOrNull { it.value.size}!!

                val lowest = next.joltages.withIndex().toList()[joltageIndex]

                for (button in toClick.filter { nullIndices.all { ni -> !it.toggleList.contains(ni.index) } }) {
                    val low = if (toClick.size == 1) lowest.value else 0
                    for (repeat in lowest.value downTo low) {
                        val newJoltages = next.joltages.withIndex().map { (i, joltage) ->
                            if (button.toggles and (1 shl i) == (1 shl i)) {
                                joltage - repeat
                            } else {
                                joltage
                            }
                        }
                        if (newJoltages.all { it == 0 }) {
                            lowestCnt = min(next.count + repeat, lowestCnt)
                            break
                        } else {
                            queue.add(MachineState2(newJoltages, next.count + repeat, next.usedButtons + button))
                        }
                    }
                }
            }
            lowestCnt
        }

    val lines = File("inputs/day10_test.txt").readLines()

    val machines = lines.map { line ->
        val split = line.split(" ")
        val lightsOn = split.first()
            .replace("[", "")
            .replace("]", "")
            .withIndex()
            .filter { it.value == '#' }
            .fold(0) { acc, i -> acc or (1 shl i.index) }
        val buttons = split.drop(1).dropLast(1).map { button ->
            Button(
                button
                .replace("(", "")
                .replace(")", "")
                .split(",")
                .fold(0) { acc, i -> acc or (1 shl i.toInt()) },
                button
                    .replace("(", "")
                    .replace(")", "")
                    .split(",")
                    .map { it.toInt() }
                    .toSet()
            )
        }
        val joltages = split.last()
            .replace("{", "")
            .replace("}", "")
            .split(",")
            .map { it.toInt() }

        Machine(lightsOn, buttons, joltages)
    }
    println(part1(machines))
    //println(part2(machines))
    println(part2Z3(machines))
}

data class Button(val toggles: Int, val toggleList: Set<Int>)

data class Machine(val lightsOn: Int, val buttons: List<Button>, val joltages: List<Int>)
