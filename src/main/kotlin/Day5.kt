import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {
    fun part1(ranges: List<LongRange>, items: List<Long>): Int =
        items.count { item -> ranges.any { item in it } }

    fun part2(startRanges: Set<LongRange>): Long {
        var ranges = startRanges

        do {
            val oldRanges = ranges
            val newRanges = mutableSetOf<LongRange>()
            for (range in ranges) {
                val overlap = newRanges.find { other ->
                    (range.start < other.start && range.endInclusive > other.endInclusive) ||
                    (range.start < other.start && other.contains(range.endInclusive)) ||
                    (range.endInclusive > other.endInclusive && other.contains(range.start)) ||
                    (other.contains(range.start) && other.contains(range.endInclusive))
                }
                if (overlap != null) {
                    newRanges.remove(overlap)
                    newRanges.add(min(range.start, overlap.start)..max(range.endInclusive, overlap.endInclusive))
                } else {
                    newRanges.add(range)
                }
            }
            ranges = newRanges
        } while (oldRanges != newRanges)

        return ranges.sumOf { it.endInclusive - it.start + 1 }
    }

    val lines = File("inputs/day5.txt").readLines()
    val ranges = lines.takeWhile { it != "" }.map {
        val (first, second) = it.split("-")
        first.toLong()..second.toLong()
    }
    val items = lines.dropWhile { it != "" }.drop(1).map { it.toLong() }

    println(part1(ranges, items))
    println(part2(ranges.toSet()))
}
