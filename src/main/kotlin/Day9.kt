import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.min
import  kotlin.math.max

fun main(args: Array<String>) {
    fun part1(combinations: List<Pair<Coordinate, Coordinate>>): Long =
        combinations.first().area()

    fun part2(coords: List<Coordinate>, combinations: List<Pair<Coordinate, Coordinate>>): Long {
        val verticals = coords.map { coord ->
            val vertical = coords.find { it.x == coord.x }!!
            coord.x to (min(coord.y, vertical.y)..max(coord.y, vertical.y))
        }.toMap()

        val horizontals = coords.map { coord ->
            val vertical = coords.find { it.y == coord.y }!!
            coord.y to (min(coord.x, vertical.x)..max(coord.x, vertical.x))
        }.toMap()

        return combinations.first { (f, s) ->
            val minX = min(f.x, s.x)
            val maxX = max(f.x, s.x)
            val minY = min(f.y, s.y)
            val maxY = max(f.y, s.y)

            val rectHorizontal = (minX+1) until maxX
            val rectVertical = (minY+1) until maxY

            val crossesTop = verticals.none { (atX, vertical) -> vertical.contains(minY+1) && rectHorizontal.contains(atX) }
            val crossesBottom = verticals.none { (atX, vertical) -> vertical.contains(maxY-1) && rectHorizontal.contains(atX) }
            val crossesLeft = horizontals.none { (atY, horizontal) -> horizontal.contains(minX+1) && rectVertical.contains(atY) }
            val crossesRight = horizontals.none { (atY, horizontal) -> horizontal.contains(maxX-1) && rectVertical.contains(atY) }

            crossesTop && crossesBottom && crossesLeft && crossesRight
        }.area()
    }

    val lines = File("inputs/day9.txt").readLines()
    val coords = lines.map {
        val (x, y) = it.split(",")
        Coordinate(x.toLong(), y.toLong())
    }

    val combinations = mutableListOf<Pair<Coordinate, Coordinate>>()

    for ((i, coord) in coords.withIndex()) {
        for (other in coords.drop(i + 1)) {
            combinations.add(coord to other)
        }
    }

    val sortedCombinations = combinations.sortedByDescending { it.area() }

    println(part1(sortedCombinations))
    println(part2(coords, sortedCombinations))
}

fun Pair<Coordinate, Coordinate>.area() =
    ((this.first.x - this.second.x).absoluteValue + 1) * ((this.first.y - this.second.y).absoluteValue + 1)

data class Coordinate(val x: Long, val y: Long)