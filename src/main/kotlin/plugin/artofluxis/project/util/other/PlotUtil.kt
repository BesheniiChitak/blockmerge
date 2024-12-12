package plugin.artofluxis.project.util.other

import kotlinx.serialization.Serializable
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.WorldBorder
import org.bukkit.entity.Player
import plugin.artofluxis.project.plotWorld

var plots: HashMap<Int, Plot> = hashMapOf()
val playersPlotPositions = hashMapOf<String, Int>()
val plotsBossbars = hashMapOf<Int, BossBar>()

@Serializable
class PlotWorldLocation(
    var x: Int,
    var z: Int,
) {
    fun position(): Location {
        return Location(plotWorld, x + 0.5, 16.5, z + 0.5)
    }

    override fun toString(): String {
        return "{x: $x, z: $z}"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is PlotWorldLocation) x == other.x && z == other.z else false
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

fun List<PlotWorldLocation>.containsLocation(loc: PlotWorldLocation): Boolean {
    return this.any { it.x == loc.x && it.z == loc.z }
}

fun Location.toPlotWorldLocation(): PlotWorldLocation {
    return PlotWorldLocation(x.toInt(), z.toInt())
}

@Serializable
data class PlotUpgrades(
    var blockLevel: Int = 0,
)

@Serializable
data class PlotStats(
    var upgrades: PlotUpgrades = PlotUpgrades(),
    var bestUnlockedBlock: Int = 0,
    var notation: Notation = Notation.NORMAL,
    var money: BigInteger = BigInteger("0"),
)

@Serializable
class Plot(
    private val position: Int,
) {
    var owner: String? = null
    private var location: PlotWorldLocation? = null
    var currentSelectedBlock: PlotWorldLocation? = null
    val placeableLocations = mutableListOf<PlotWorldLocation>()

    val centerLocation: Location?
        get() = location?.position()?.add(4.0, 0.0, 4.0)

    private var blockSpawnTimer = 0
    private var blockEarnTimer = 0

    val stats = PlotStats()

    init {
        plotsBossbars[position] = BossBar.bossBar(
            text(""), 0f, BossBar.Color.PURPLE, BossBar.Overlay.NOTCHED_10
        )
    }

    fun create(owner: Player): Plot {
        val coordinates = calculateCoordinates(position)

        val x = coordinates.first
        val z = coordinates.second

        location = PlotWorldLocation(x, z)

        plotWorld.getChunkAt(x / 16, z / 16)
        plotWorld.getChunkAt(x / 16 + 1, z / 16)
        plotWorld.getChunkAt(x / 16, z / 16 + 1)
        plotWorld.getChunkAt(x / 16 + 1, z / 16 + 1)
        plotWorld.getChunkAt(x / 16 - 1, z / 16)
        plotWorld.getChunkAt(x / 16, z / 16 - 1)
        plotWorld.getChunkAt(x / 16 - 1, z / 16 - 1)


        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            "clone from minecraft:overworld 0 -60 0 8 -60 8 to minecraft:plotworld $x 16 $z"
        )

        for (xCheck in x..x + 8) {
            for (zCheck in z..z + 8) {
                val loc = PlotWorldLocation(xCheck, zCheck)
                val pos = loc.position()

                if (pos.block.type == Material.POLISHED_ANDESITE) {
                    placeableLocations.add(loc)
                }

            }
        }

        this.owner = owner.name

        plots[position] = this
        playersPlotPositions[owner.name] = position
        return this
    }

    fun tick() { // function called every 2 ticks (0.1 seconds)
        if (placeableLocations.isNotEmpty() && ++blockSpawnTimer >= calculateSpawnTimer()) {
            blockSpawnTimer = 0
            for (loc in placeableLocations) {
                if (loc == currentSelectedBlock) continue
                val pos = loc.position().add(0.0, 1.0, 0.0)
                if (pos.block.type == Material.AIR) {
                    pos.block.type = blocks[stats.upgrades.blockLevel]
                    break
                }
            }
        }
        if (++blockEarnTimer >= 15) {
            blockEarnTimer = 0
            for (loc in placeableLocations) {
                val pos = loc.position().add(0.0, 1.0, 0.0)
                if (pos.block.type != Material.AIR) {
                    stats.money += blocksToEarnings[pos.block.type]!!
                }
            }
        }
    }

    fun getOwner(): Player? {
        return owner?.let { Bukkit.getPlayer(it) }
    }

    private fun calculateSpawnTimer(): Int {
        return 50
    }

    val worldBorder: WorldBorder
        get() {
            val border = Bukkit.createWorldBorder()
            border.center = this.centerLocation!!
            border.size = 9.0
            border.warningDistance = 0
            return border
        }

    val bossBar: BossBar
        get() = plotsBossbars[position]!!
            .name(
                translatable(
                    "sys.until_next_block",
                    text((calculateSpawnTimer() - blockSpawnTimer.toDouble()) / 10.0)
                )
            )
            .progress(blockSpawnTimer / 50f)
}

fun findEmptyPosition(): Int {
    var position = 0
    while (plots.containsKey(position)) {
        position++
    }
    return position
}

fun findPlot(playerName: String): Plot? {
    plots.forEach { plot ->
        if (playerName == plot.value.owner) {
            playersPlotPositions[playerName] = plot.key
            return plot.value
        }
    }
    return null
}

const val X_SIZE = 2001
const val DOUBLE_SIZE = X_SIZE * 2

fun calculateCoordinates(position: Int): Pair<Int, Int> {
    val coordinate = (position * 9)
    val x = coordinate % DOUBLE_SIZE - X_SIZE
    val z = coordinate / DOUBLE_SIZE - X_SIZE
    return x to z
}

fun Player.hasPlot() = this.plot != null

val Player.plot: Plot?
    get() = plots[playersPlotPositions[name]] ?: findPlot(name)

