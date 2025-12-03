import java.io.File

val inputRegex = """([RL])([0-9]+)""".toRegex()

fun main(args: Array<String>) {
    fun part1(lines: List<Line>): Int {
        val positions = lines.runningFold(50) { pos, direction ->
            val newPos = (when (direction.direction) {
                "L" -> pos - direction.distance
                "R" -> pos + direction.distance
                else -> throw Exception("Unknown direction")
            }) % 100

            if (newPos < 0) 100 + newPos else newPos
        }
        return positions.count { it == 0 }
    }

    fun part2(lines: List<Line>): Int {
        val positions = lines.runningFold(listOf(50)) { pos, direction ->
            val ranges = when (direction.direction) {
                "L" -> pos.last() downTo (pos.last() - direction.distance)
                "R" -> pos.last()..(pos.last() + direction.distance)
                else -> throw Exception("Unknown direction")
            }

            ranges.toList().drop(1).map { it.mod(100) }
        }.flatten()
        return positions.count { it == 0 }
    }

    val lines = File("inputs/day1.txt").readLines()
    val input = lines.map {
        val result = inputRegex.findAll(it).first()
        Line(result.groupValues[1], result.groupValues[2].toInt())
    }

    println(part1(input))
    println(part2(input))
}

data class Line(val direction: String, val distance: Int)