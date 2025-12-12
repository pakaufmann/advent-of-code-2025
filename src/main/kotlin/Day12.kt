import java.io.File

val idRegex = "[0-9]+:".toRegex()
val region = "[0-9]+x[0-9]+:.*".toRegex()

fun main(args: Array<String>) {
    fun countLayout(layout: List<Long>) =
        layout.sumOf {
            var cnt = 0
            var n = it
            while (n > 0) {
                n  = n and (n - 1)
                cnt++
            }
            cnt
        }

    fun canFit(region: Region, shapes: Map<Int, Shape>): Boolean {
        val cache = mutableMapOf<Pair<List<Long>, List<Shape>>, Boolean>()

        if (region.startShapes(shapes).sumOf { 9L } <= region.area) {
            return true
        }
        if (region.startShapes(shapes).sumOf { it.size } > region.area) {
            return false
        }

        // Basically useless, as in practice this is never hit. All shapes either have ample space
        // or not enough space at all.
        fun canFitInternal(layout: List<Long>, remainingShapes: List<Shape>): Boolean {
            val cached = cache[layout to remainingShapes]
            if (cached != null) {
                return cached
            }

            return cache.getOrPut(layout to remainingShapes) {
                if (remainingShapes.isEmpty()) return@getOrPut true

                val filledCount = countLayout(layout)
                val remainingSpace = region.area - filledCount
                val remainingToPlace = remainingShapes.sumOf { it.size }
                if (remainingToPlace > remainingSpace) {
                    return false
                }

                val next = remainingShapes.first()

                for (rotation in next.rotations()) {
                    for (dx in 0..(region.width - rotation.width)) {
                        for (dy in 0..(region.height - rotation.height)) {
                            if ((layout[dy] and (1L shl dx)) == (1L shl dx)) {
                                continue
                            }

                            val movedY = List(dy) { 0L } + rotation.bitmapLayout
                            val movedX = movedY.map { it shl dx }
                            val newLayout = layout.withIndex().map { (i, row) ->
                                row or movedX.getOrElse(i) { 0L }
                            }
                            if (filledCount + rotation.size == countLayout(newLayout)) {
                                if (canFitInternal(newLayout, remainingShapes.drop(1))) {
                                    return@getOrPut true
                                }
                            }
                        }
                    }
                }

                return@getOrPut false
            }
        }

        return canFitInternal(region.emptyLayout(), region.startShapes(shapes))
    }

    fun part1(shapes: Map<Int, Shape>, regions: List<Region>): Int =
        regions.count { region ->
            val fits = canFit(region, shapes)
            fits
        }

    val lines = File("inputs/day12.txt").readLines()

    val shapes = mutableMapOf<Int, Shape>()
    var currentShape = mutableListOf<List<Boolean>>()
    var id = 0

    for (line in lines.takeWhile { !region.matches(it) }.drop(1)) {
        if (idRegex.matches(line)) {
            continue
        }
        if (line.isEmpty()) {
            shapes[id] = Shape(currentShape)
            currentShape = mutableListOf()
            id++
        } else {
            currentShape.add(line.map { it == '#' })
        }
    }

    val regions = lines.dropWhile { !region.matches(it) }
        .map { region ->
            val (area, presents) = region.split(": ")
            val (width, height) = area.split("x")

            Region(
                width.toInt(),
                height.toInt(),
                presents.split(" ")
                    .withIndex()
                    .associateBy { it.index }
                    .mapValues { it.value.value.toInt() }
                    .filter { it.value > 0 }
            )
        }

    println(part1(shapes, regions))
}

data class Shape(val shape: List<List<Boolean>>) {
    val width = shape[0].size
    val height = shape.size
    val size = shape.sumOf { it.count { it } }

    val bitmapLayout = shape.map { line ->
        line.withIndex().fold(0L) { acc, n ->
            if (!n.value) {
                acc
            } else {
                acc or (1L shl n.index)
            }
        }
    }

    fun rotations(): List<Shape> =
        listOf(this, this.rotate(), this.rotate().rotate(), this.rotate().rotate().rotate())

    fun rotate(): Shape =
        Shape(shape.transpose().map { it.reversed() })
}

data class Region(val width: Int, val height: Int, val requiredPresents: Map<Int, Int>) {
    val area = width * height

    fun emptyLayout() = List(height) { 0L }

    fun startShapes(shapes: Map<Int, Shape>): List<Shape> =
        requiredPresents.flatMap { (shapeIndex, count) ->
            List(count) { shapes[shapeIndex]!! }
        }
}