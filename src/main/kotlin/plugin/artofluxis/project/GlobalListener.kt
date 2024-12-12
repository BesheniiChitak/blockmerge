package plugin.artofluxis.project

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerListPingEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import plugin.artofluxis.project.util.item
import plugin.artofluxis.project.util.key
import plugin.artofluxis.project.util.other.*

object GlobalListener : Listener {

    private val playerInteractionCooldown = hashMapOf<String, Boolean>()

    @EventHandler
    fun onServerListPing(event: ServerListPingEvent) {
        event.maxPlayers = event.numPlayers + 1
        event.motd(text("AAAAAAAAAAAA"))
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        event.quitMessage(text("▶ ").color(MColor.RED) + player.name().color(MColor.WHITE))
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        var msg = text("▶ ").color(MColor.LIME) + player.name().color(MColor.WHITE)

        player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED)?.baseValue = 0.0
        player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE)?.baseValue = 8.0

        val plot = if (player.hasPlot()) {
            player.plot!!
        } else {
            msg += text(" ғɪʀsᴛ ᴊᴏɪɴ").color(MColor.PURPLE)
            Plot(findEmptyPosition()).create(player)
        }
        player.teleport(plot.centerLocation?.add(0.0, 0.5, 0.0)!!)
        event.joinMessage(msg)
        runTaskLater(3) {
            player.worldBorder = plot.worldBorder
            player.sendMessage(translatable("sys.plot_loaded").color(MColor.LIME))
            player.setDefaultInventory()
        }
    }

    @EventHandler
    fun onBlockCollect(event: PlayerInteractEvent) {
        val player = event.player
        if (playerInteractionCooldown[player.name] == true || event.clickedBlock?.location?.y?.toInt() != 17 || event.hand != EquipmentSlot.HAND || event.action != Action.LEFT_CLICK_BLOCK || !player.hasPlot()) return

        playerInteractionCooldown[player.name] = true
        runTaskLater(4) {
            playerInteractionCooldown[player.name] = false
        }

        val plot = player.plot!!

        val block = event.clickedBlock!!
        val pos = block.location
        val loc = pos.toPlotWorldLocation()


        if (!plot.placeableLocations.containsLocation(loc)) return

        if (plot.currentSelectedBlock == null) {
            plot.currentSelectedBlock = loc

            player.inventory.heldItemSlot = 4
            player.inventory.setItem(4, item(block.type) {
                this.itemName(translatable(block.type.translationKey()).color(MColor.GOLD))
                this.persistentDataContainer.set(key("earnblock"), PersistentDataType.BOOLEAN, true)
                this.isHideTooltip = true
            })
            block.type = Material.AIR
        } else {
            val item = player.inventory.getItem(4) ?: return
            if (player.inventory.heldItemSlot == 4 && item.type == block.type) {
                val index = blocks.indexOf(block.type) + 1
                if (index == blocks.size) {
                    player.sendMessage(translatable("sys.max_lvl_block").color(MColor.RED))
                    return
                }
                player.inventory.setItem(4, ItemStack(Material.AIR))
                block.type = blocks[index]
                plot.currentSelectedBlock = null
            }
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        val plot = player.plot
        if (event.blockPlaced.location.y.toInt() != 17 || plot == null || !event.itemInHand.persistentDataContainer.has(
                key("earnblock")
            ) || event.blockPlaced.location.toPlotWorldLocation() !in plot.placeableLocations
        ) {
            event.isCancelled = true
            return
        }
        plot.currentSelectedBlock = null
    }

    @EventHandler
    fun onItemClick(event: PlayerInteractEvent) {
        val player = event.player
        if (event.hand != EquipmentSlot.HAND || !event.action.isRightClick || !player.hasPlot()) return
        val item = player.inventory.itemInMainHand
        (item.persistentDataContainer[key("action"), ItemActionType()] ?: return).call(event)
    }
}