import java.io.File

fun main(args: Array<String>) {
    fun part1(problems: List<List<String>>): Long =
        problems.sumOf { problem ->
            when(problem.last()) {
                "+" -> problem
                    .dropLast(1)
                    .sumOf { it.toLong() }
                "*" -> problem
                    .dropLast(1)
                    .fold(1L) { acc, n -> acc * n.toLong() }
                else -> 0
            }
        }

    fun part2(problems: List<List<String>>): Long =
        problems.sumOf { problem ->
            val inputs = problem.dropLast(1)
            val longestIndex = inputs.maxOf { it.length }

            val numbers = (0 until longestIndex).map { i ->
                inputs
                    .map { it[i] }
                    .joinToString("")
                    .trim()
                    .toLong()
            }

            when(problem.last().trim()) {
                "+" -> numbers
                    .sumOf { it }
                "*" -> numbers
                    .fold(1L) { acc, n -> acc * n }
                else -> 0
            }
        }

    val lines = File("inputs/day6.txt").readLines()
    val problems = lines.map { line ->
        line.trim().replace("  +".toRegex(), " ")
            .split(" ")
    }.transpose()

    val problems2 = mutableListOf<List<String>>()
    var last = 0
    for(i in lines.first().indices) {
        if (lines.all { it[i] == ' ' }) {
            problems2.add(
                lines.map { it.substring(last, i) }
            )
            last = i + 1
        }
    }

    val lastProblem = lines.map { it.substring(last) }
    val longest = lastProblem.maxOf { it.length }
    problems2.add(lastProblem.map { it.padEnd(longest) })

    println(part1(problems))
    println(part2(problems2))
}

fun <T>List<List<T>>.transpose(): List<List<T>> =
    this[0].indices.map { i -> (this.indices).map { j -> this[j][i] } }
