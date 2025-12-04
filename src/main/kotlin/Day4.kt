import java.io.File

fun main(args: Array<String>) {
    fun accessible(paperRolls: Set<Pair<Int, Int>>): List<Pair<Int, Int>> =
        paperRolls.filter { roll -> roll.neighbours().count { paperRolls.contains(it) } < 4 }

    fun part1(paperRolls: Set<Pair<Int, Int>>): Int =
        accessible(paperRolls).count()

    fun part2(paperRolls: Set<Pair<Int, Int>>): Int {
        val stableState =
            generateSequence(paperRolls) {
                it - accessible(it)
            }.windowed(2)
                .first { it.first() == it.last() }
                .first()
        return paperRolls.size - stableState.size
    }

    val lines = File("inputs/day4.txt").readLines()

    val paperRolls = lines.withIndex().flatMap { (y, line) ->
        line.withIndex().filter { (_, char) ->
            char == '@'
        }.map { (x, _) -> Pair(x, y) }
    }.toSet()

    println(part1(paperRolls))
    println(part2(paperRolls))
}

fun Pair<Int, Int>.neighbours(): List<Pair<Int, Int>> =
    (-1..1).flatMap { dx ->
        (-1..1).map { dy ->
            Pair(this.first + dx, this.second + dy)
        }
    }.filter { it != this }
