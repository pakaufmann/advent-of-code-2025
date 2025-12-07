import java.io.File

fun main(args: Array<String>) {
    fun part1(start: Position, splitter: Set<Position>): Int {
        val positions = mutableListOf(start)
        val splits = mutableSetOf<Position>()
        val lowestSplitter = splitter.maxOf { it.y }

        while (positions.isNotEmpty()) {
            val next = positions.removeFirst()
            val nextDown = next.copy(y = next.y + 1)

            if (!splits.contains(nextDown)) {
                if (splitter.contains(nextDown)) {
                    splits.add(nextDown)
                    positions.add(nextDown.copy(x = nextDown.x - 1))
                    positions.add(nextDown.copy(x = nextDown.x + 1))
                } else if (nextDown.y <= lowestSplitter) {
                    positions.add(nextDown)
                }
            }
        }

        return splits.size
    }

    val cache = mutableMapOf<Position, Long>()

    fun followSplits(start: Position, splitter: Set<Position>): Long =
        cache.getOrPut(start) {
            var nextPosition = start
            val lowestSplitter = splitter.maxOf { it.y }

            while (nextPosition.y < lowestSplitter) {
                nextPosition = nextPosition.copy(y = nextPosition.y + 1)
                if (splitter.contains(nextPosition)) {
                    val left = nextPosition.copy(x = nextPosition.x - 1)
                    val right = nextPosition.copy(x = nextPosition.x + 1)
                    return@getOrPut followSplits(left, splitter) + followSplits(right, splitter)
                }
            }

            return@getOrPut 1
        }

    fun part2(start: Position, splitter: Set<Position>): Long =
        followSplits(start, splitter)

    val lines = File("inputs/day7.txt").readLines()

    val types = lines.withIndex().flatMap { (y, line) ->
        line.withIndex().map { (x, char) ->
            if (char == '^') {
                Pair(Position(x, y), "splitter")
            } else if (char == 'S') {
                Pair(Position(x, y), "start")
            } else {
                null
            }
        }
    }.filterNotNull()
    val splitters = types.filter { it.second == "splitter" }.map { it.first }.toSet()
    val start = types.find { it.second == "start" }?.first!!

    println(part1(start, splitters))
    println(part2(start, splitters))
}

data class Position(val x: Int, val y: Int)