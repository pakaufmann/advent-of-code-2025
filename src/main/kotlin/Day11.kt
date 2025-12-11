import java.io.File

fun main(args: Array<String>) {
    fun countPaths(rackMap: Map<String, ServerRack>, from: String, to: String): Long {
        val cache = mutableMapOf<String, Long>()
        fun count(at: String): Long =
            cache.getOrPut(at) {
                if (at == to) {
                    return 1
                }
                rackMap[at]?.connections?.sumOf { count(it) } ?: 0
            }
        return count(from)
    }

    fun part1(racks: List<ServerRack>): Long {
        val rackMap = racks.associateBy { it.name }
        return countPaths(rackMap, "you", "out")
    }

    fun part2(racks: List<ServerRack>): Long {
        val rackMap = racks.associateBy { it.name }
        val toFftCount = countPaths(rackMap, "svr", "fft")
        val toDacCount = countPaths(rackMap, "fft", "dac")
        val toOutCount = countPaths(rackMap, "dac", "out")
        return toFftCount * toDacCount * toOutCount
    }

    val lines = File("inputs/day11.txt").readLines()
    val mapped = lines.map { line ->
        val (name, connections) = line.split(": ")
        ServerRack(name, connections.split(" ").toSet())
    }
    println(part1(mapped))
    println(part2(mapped))
}

data class ServerRack(val name: String, val connections: Set<String>)

