import java.io.File

fun main(args: Array<String>) {
    fun calculateJoltage(batteriesList: List<List<Int>>, batteriesToTurnOn: Int): Long =
        batteriesList.sumOf { batteries ->
            batteries
                .withIndex()
                .fold(listOf<Int>()) { turnedOnBatteries, (i, batteryJoltage) ->
                    val remaining = batteries.size - i
                    val previousLowerJoltage = turnedOnBatteries.indexOfFirst { it < batteryJoltage }
                    when {
                        previousLowerJoltage != -1 -> {
                            val removeUpTo = previousLowerJoltage.coerceAtLeast(batteriesToTurnOn - remaining)
                            turnedOnBatteries.take(removeUpTo) + batteryJoltage
                        }
                        turnedOnBatteries.size < batteriesToTurnOn ->
                            turnedOnBatteries + batteryJoltage
                        else -> turnedOnBatteries
                    }
                }
                .fold(0L) { acc, i -> acc * 10 + i }
        }

    val lines = File("inputs/day3.txt").readLines()
    val batteriesList = lines.map { it.map { n -> n.toString().toInt() } }

    println(calculateJoltage(batteriesList, 2))
    println(calculateJoltage(batteriesList, 12))
}