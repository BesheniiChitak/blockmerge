package plugin.artofluxis.project.util.guis

import net.kyori.adventure.text.Component.text
import org.bukkit.Material
import org.bukkit.entity.Player
import plugin.artofluxis.project.util.item
import plugin.artofluxis.project.util.other.InventoryItem
import plugin.artofluxis.project.util.other.InventoryMenu

object ExampleMenu {

    private val a = item(Material.LIGHT_GRAY_STAINED_GLASS_PANE) { isHideTooltip = true } to InventoryItem(true)
    private val s = item(Material.GRAY_STAINED_GLASS_PANE) { isHideTooltip = true } to InventoryItem(true)

    fun open(player: Player) {
        InventoryMenu(player, 4, text("menu")) {
            this.setItems(
                listOf(
                    s, s, s, s, s, s, s, s, s,
                    s, a, a, a, a, a, a, a, s,
                    s, a, a, a, a, a, a, a, s,
                    s, s, s, s, s, s, s, s, s
                )
            )
        }.open()
    }
}