package plugin.artofluxis.project.util.guis

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Material
import org.bukkit.entity.Player
import plugin.artofluxis.project.util.item
import plugin.artofluxis.project.util.other.InventoryButton
import plugin.artofluxis.project.util.other.InventoryItem
import plugin.artofluxis.project.util.other.InventoryMenu

object UpgradesMenu {

    private val a = item(Material.LIGHT_GRAY_STAINED_GLASS_PANE) { isHideTooltip = true } to InventoryItem(true)
    private val s = item(Material.GRAY_STAINED_GLASS_PANE) { isHideTooltip = true } to InventoryItem(true)

    private val N = item(Material.BRICK) {
        this.itemName(text("ᴅᴇғᴀᴜʟᴛ ᴜᴘɢʀᴀᴅᴇs"))
    } to InventoryButton {
        openNormalUpgrades(this.whoClicked as Player)
    }
    private val S = item(Material.GOLD_INGOT) {
        this.itemName(text("sᴛᴀʀᴅᴜsᴛ ᴜᴘɢʀᴀᴅᴇs"))
    } to InventoryButton {
        openStardustUpgrades(this.whoClicked as Player)
    }

    fun openNormalUpgrades(player: Player) {
        InventoryMenu(player, 5, text("ᴅᴇғᴀᴜʟᴛ ᴜᴘɢʀᴀᴅᴇs")) {
            this.setItems(
                listOf(
                    N, s, a, a, a, a, a, s, a,
                    S, s, a, a, a, a, a, s, a,
                    a, s, a, a, a, a, a, s, a,
                    a, s, a, a, a, a, a, s, a,
                    a, s, a, a, a, a, a, s, a
                )
            )
        }.open()
    }

    fun openStardustUpgrades(player: Player) {
        InventoryMenu(player, 5, text("sᴛᴀʀᴅᴜsᴛ ᴜᴘɢʀᴀᴅᴇs")) {
            this.setItems(
                listOf(
                    N, s, a, a, a, a, a, s, a,
                    S, s, a, a, a, a, a, s, a,
                    a, s, a, a, a, a, a, s, a,
                    a, s, a, a, a, a, a, s, a,
                    a, s, a, a, a, a, a, s, a
                )
            )
        }.open()
    }
}