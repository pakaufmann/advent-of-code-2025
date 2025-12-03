import java.io.File

fun main(args: Array<String>) {
    fun part1(numbers: List<Long>): Long {
        return numbers.filter {
            val numStr = it.toString()
            val middle = numStr.length / 2
            numStr.take(middle) == numStr.drop(middle)
        }.sum()
    }

    fun split(str: String, length: Int): List<String> {
        if (str.isEmpty()) return emptyList()
        return listOf(str.take(length)) + split(str.drop(length), length)
    }

    fun part2(numbers: List<Long>): Long {
        return numbers.filter {
            val numStr = it.toString()
            val middle = numStr.length / 2

            (1..middle).any {
                val splits = split(numStr, it)
                splits.all { it == splits[0] }
            }
        }.sum()
    }

    val lines = File("inputs/day2.txt").readLines()
    val numbers = lines[0].split(",").flatMap {
        val (from, to) = it.split("-")
        (from.toLong()..to.toLong()).toList()
    }

    println(part1(numbers))
    println(part2(numbers))
}