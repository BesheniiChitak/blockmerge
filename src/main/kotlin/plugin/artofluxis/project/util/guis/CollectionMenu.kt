package plugin.artofluxis.project.util.guis

import net.kyori.adventure.text.Component.text
import org.bukkit.Material
import org.bukkit.entity.Player
import plugin.artofluxis.project.util.item
import plugin.artofluxis.project.util.other.*

object CollectionMenu {

    private val noBlock = item(Material.BLACK_CONCRETE) { isHideTooltip = true }
    private val menuBorder = item(Material.GRAY_STAINED_GLASS_PANE) { isHideTooltip = true }

    fun openCollection(player: Player, page: Int) {
        InventoryMenu(player, 5, text("ʙʟᴏᴄᴋ ᴄᴏʟʟᴇᴄᴛɪᴏɴ")) {
            val plot = player.plot ?: return@InventoryMenu

            val bestBlock = plot.stats.bestUnlockedBlock.coerceAtMost(blocks.size)

            this.setItems(0..35, noBlock, true)
            this.setItems(36..44, menuBorder, true)

            blocks.subList(
                ((page - 1) * 36).coerceAtMost(bestBlock),
                (page * 36).coerceAtMost(bestBlock)
            ).forEachIndexed { i, item ->
                this.setItem(i, item(item) {
                    this.lore(listOf(
                            text(blocksToEarnings[item].toString())
                        )
                    )
                }, true)
            }
        }.open()
    }
}