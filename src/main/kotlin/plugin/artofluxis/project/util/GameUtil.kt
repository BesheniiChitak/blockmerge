package plugin.artofluxis.project.util

import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.state.BlockState
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.craftbukkit.block.CraftBlock
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

// Extension to get BlockPos from Location
val Location.blockPos: BlockPos
    get() = BlockPos(x.toInt(), y.toInt(), z.toInt())

// Extension to get the NMS player from a Bukkit player
val Player.nms: ServerPlayer
    get() = (this as CraftPlayer).handle

// Extension to get the NMS block state from a Bukkit block
val Block.nms: BlockState
    get() = (this as CraftBlock).nms

/**
 * Function to display block breaking animation at a specific position.
 * @param source - the player who initiated the block breaking.
 * @param progress - the progress of the destruction (from 1 to 10).
 * @param showToEveryone - if true, shows the animation to all players in the world.
 */
fun Location.setBlockDestruction(source: Player, progress: Int, showToEveryone: Boolean = false) {
    val dec = progress - 1 // Progress is displayed from 0 to 9, so decrement the value by 1
    val pos = blockPos // Get block position
    val hash = pos.hashCode() // Generate a unique hash for this position

    // Determine which players to show the destruction animation to
    val players = if (showToEveryone) world.players else listOf(source)

    // Send a packet to each player to display the block destruction animation
    players.forEach { player ->
        player.uniqueId
        player.nms.connection.send(
            ClientboundBlockDestructionPacket(
                hash, // Use the hash to identify the destruction
                pos,  // Block position
                dec   // Progress of the destruction
            )
        )
    }
}

class Region(
    val corner1: Location,
    val corner2: Location,
    private val ignoreY: Boolean = false,
) {
    private val world: World = corner1.world

    init {
        if (world.name != corner2.world.name) throw IllegalArgumentException("A region was created from two locations in different worlds.")
    }

    private val first = Location(
        world,
        min(corner1.x, corner2.x),
        if (ignoreY) -64.0 else min(corner1.y, corner2.y),
        min(corner1.z, corner2.z)
    )
    private val second = Location(
        world,
        max(corner1.x, corner2.x),
        if (ignoreY) 320.0 else max(corner1.y, corner2.y),
        max(corner1.z, corner2.z)
    )

    // Iterate over all blocks in the region and perform the given action
    fun iterate(action: (x: Int, y: Int, z: Int) -> Unit) {
        for (x in first.x.toInt()..second.x.toInt()) {
            for (y in first.y.toInt()..second.y.toInt()) {
                for (z in first.z.toInt()..second.z.toInt()) {
                    action(x, y, z)
                }
            }
        }
    }

    // Iterate over all blocks with offset indices relative to the first point
    fun iterateWithIndexes(action: (x: Int, y: Int, z: Int, xIndex: Int, yIndex: Int, zIndex: Int) -> Unit) {
        val firstX = first.x.toInt()
        val firstY = first.y.toInt()
        val firstZ = first.z.toInt()
        val secondX = second.x.toInt()
        val secondY = second.y.toInt()
        val secondZ = second.z.toInt()
        for (x in firstX..secondX) {
            for (y in firstY..secondY) {
                for (z in firstZ..secondZ) {
                    action(x, y, z, x - firstX, y - firstY, z - firstZ)
                }
            }
        }
    }

    /**
     * Copy all blocks from the current region to a new location, considering the source region center and the paste center.
     * @param sourceCenter - center of the source region from where blocks are copied.
     * @param pasteCenter - center of the paste region where blocks are added.
     */
    fun copyTo(sourceCenter: Location, pasteCenter: Location) {

        Bukkit.getConsoleSender()
            .sendMessage("Region size: (${second.x - first.x}, ${second.y - first.y}, ${second.z - first.z})")

        val sourceWorld = sourceCenter.world
        val pasteWorld = pasteCenter.world

        // Determine the offset for pasting based on region corners
        val xOffset = pasteCenter.blockX - sourceCenter.blockX
        val yOffset = pasteCenter.blockY - sourceCenter.blockY
        val zOffset = pasteCenter.blockZ - sourceCenter.blockZ

        // Iterate over the regions and copy blocks
        iterate { x, y, z ->
            val sourceBlock = sourceWorld.getBlockAt(x, y, z)
            val pasteBlock = pasteWorld.getBlockAt(
                (x + xOffset),
                (y + yOffset),
                (z + zOffset)
            )
            pasteBlock.blockData = sourceBlock.blockData
        }
    }
    fun randomLocation(): Location {
        return Location(
            world,
            Random.nextDouble(corner1.x, corner2.x),
            Random.nextDouble(corner1.y, corner2.y),
            Random.nextDouble(corner1.z, corner2.z)
        )
    }
}

/**
 * Converts a string into a Region object.
 * @param region - string containing information about the world, Y-ignore flag, and corner coordinates of the region.
 * @return Region - a region created based on the string.
 */
fun toRegion(region: String): Region {
    val corners = region.split(" | ")
    val world = Bukkit.getWorld(corners[0])
    val corner1List = corners[2].split(", ").map { it.toDouble() }
    val corner2List = corners[3].split(", ").map { it.toDouble() }
    return Region(
        Location(world, corner1List[0], corner1List[1], corner1List[2]),
        Location(world, corner2List[0], corner2List[1], corner2List[2]),
        corners[1].toBoolean()
    )
}