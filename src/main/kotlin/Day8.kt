import java.io.File
import java.util.*
import kotlin.collections.LinkedHashSet
import kotlin.math.pow
import kotlin.math.sqrt

fun main(args: Array<String>) {
    fun getCircuits(edgeWeights: LinkedList<Pair<JunctionBox, JunctionBox>>): List<LinkedHashSet<JunctionBox>> {
        val circuits = mutableListOf<LinkedHashSet<JunctionBox>>()

        while (edgeWeights.isNotEmpty()) {
            val (from, to) = edgeWeights.removeFirst()

            val firstCircuit = circuits.indexOfFirst { it.contains(from) }
            val secondCircuit = circuits.indexOfFirst { it.contains(to) }

            if (firstCircuit == -1 && secondCircuit == -1) {
                circuits.add(linkedSetOf(from, to))
            } else if (firstCircuit != -1 && secondCircuit != -1) {
                if (firstCircuit == secondCircuit) {
                    continue
                }
                val first = circuits[firstCircuit]
                circuits[secondCircuit].addAll(first)
                circuits.removeAt(firstCircuit)
            } else if (firstCircuit != -1) {
                circuits[firstCircuit].remove(from)
                circuits[firstCircuit].add(from)
                circuits[firstCircuit].add(to)
            } else if (secondCircuit != -1) {
                circuits[secondCircuit].remove(to)
                circuits[secondCircuit].add(to)
                circuits[secondCircuit].add(from)
            }
        }
        return circuits
    }

    fun part1(edgeWeights: LinkedList<Pair<JunctionBox, JunctionBox>>): Int =
        getCircuits(edgeWeights)
            .sortedByDescending { it.size }
            .take(3)
            .fold(1) { i, box -> i * box.size }

    fun part2(edgeWeights: LinkedList<Pair<JunctionBox, JunctionBox>>): Long =
        getCircuits(edgeWeights)
            .first()
            .reversed()
            .take(2)
            .fold(1L) { i, box -> i * box.x }

    val lines = File("inputs/day8.txt").readLines()
    val junctionBoxes = lines.map {
        val (x, y, z) = it.split(",")
        JunctionBox(x.toInt(), y.toInt(), z.toInt())
    }

    val unconnected = mutableListOf(*junctionBoxes.toTypedArray())
    val edgeWeights = sortedMapOf<Double, Pair<JunctionBox, JunctionBox>>(compareBy { it })

    while (unconnected.isNotEmpty()) {
        val next = unconnected.removeLast()

        for (other in unconnected) {
            val distance = next.distanceTo(other)

            edgeWeights[distance] = next to other
        }
    }

    println(part1(LinkedList(edgeWeights.values.take(1000))))
    println(part2(LinkedList(edgeWeights.values)))
}

data class JunctionBox(val x: Int, val y: Int, val z: Int) {
    fun distanceTo(other: JunctionBox): Double =
        sqrt(
            (other.x - this.x).toDouble().pow(2) +
                    (other.y - this.y).toDouble().pow(2) +
                    (other.z - this.z).toDouble().pow(2)
        )
}