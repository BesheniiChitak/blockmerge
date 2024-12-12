package plugin.artofluxis.project.util.other

import net.kyori.adventure.text.Component
import plugin.artofluxis.project.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import kotlin.time.Duration.Companion.seconds

/**
 * Utility for simplifying the registration and unregistration of Listeners.
 */
fun Listener.unregister() = HandlerList.unregisterAll(this)
fun Listener.register() = Bukkit.getPluginManager().registerEvents(this, plugin)

/**
 * The InventoryMenu class represents an inventory menu for a player with customizable buttons and actions.
 * @param player The player for whom the menu is opened.
 * @param rows The number of rows in the inventory.
 * @param title The title of the inventory.
 * @param builder A lambda function for configuring the menu.
 */
class InventoryMenu(
    private val player: Player,
    rows: Int,
    title: Component,
    private val builder: InventoryMenu.() -> Unit = {}
) : InventoryHolder, Listener {

    private var closed = false // Flag for tracking menu closure.
    private val _inventory = Bukkit.createInventory(this, rows * 9, title) // Inventory creation.
    private val objects = HashMap<Int, InventoryObject>() // Storage of inventory objects by slot.
    private var closeHandler: InventoryCloseEvent.() -> Unit = {} // Inventory close handler.
    private var updateHandler: () -> Unit = {} // Inventory update handler.

    override fun getInventory(): Inventory = _inventory

    /**
     * Sets the inventory close handler.
     */
    fun onClose(action: InventoryCloseEvent.() -> Unit) {
        closeHandler = action
    }

    /**
     * Starts updating the menu at a specified interval.
     */
    fun updater(action: () -> Unit) {
        runTaskTimer(0.05.seconds) {
            if (closed) it.cancel() // Stop the timer if the menu is closed.
            else action() // Perform the update action.
        }
    }

    /**
     * Sets the inventory update handler.
     */
    fun update(action: () -> Unit) {
        updateHandler = action
    }

    /**
     * Performs an update of the menu using the specified handler.
     */
    fun update() {
        updateHandler()
    }

    /**
     * Adds a button to the specified inventory slot.
     */
    fun addButton(slot: Int, item: ItemStack, action: InventoryClickEvent.() -> Unit): InventoryMenu = apply {
        objects[slot] = InventoryButton(action) // Save the button's action.
        inventory.setItem(slot, item) // Place the item in the inventory.
    }

    /**
     * Sets an item in the specified inventory slot with the option to block clicks.
     */
    fun setItem(slot: Int, item: ItemStack, cancelClick: Boolean) {
        objects[slot] = InventoryItem(cancelClick) // Save the item's behavior.
        inventory.setItem(slot, item) // Place the item in the inventory.
    }

    /**
     * Sets items in multiple inventory slots with the option to block clicks.
     */
    fun setItems(slots: IntArray, item: ItemStack, cancelClick: Boolean) {
        slots.forEach { slot ->
            setItem(slot, item, cancelClick) // Use existing logic to set items.
        }
    }

    /**
     * Sets items in a range of inventory slots with the option to block clicks.
     */
    fun setItems(slots: IntRange, item: ItemStack, cancelClick: Boolean) {
        slots.forEach { slot ->
            setItem(slot, item, cancelClick)
        }
    }

    fun setItems(items: List<Pair<ItemStack, InventoryObject>>) {
        items.forEachIndexed { i, (item, obj) ->
            when (obj) {
                is InventoryButton -> addButton(i, item, obj.clickHandler)
                is InventoryItem -> setItem(i, item, obj.cancelClick)
            }
        }
    }

    /**
     * Opens the menu for the player and registers events.
     */
    fun open(): InventoryMenu {
        builder(this) // Perform menu configuration.
        register() // Register events.
        player.openInventory(inventory) // Open the inventory for the player.
        return this
    }

    /**
     * Handles inventory click events.
     */
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val clickedInventory = event.clickedInventory ?: return
        if (clickedInventory.holder != this) return // Ensure the clicked inventory belongs to this menu.

        val slot = event.slot
        objects[slot]?.apply { // Process the action based on the type of object.
            when (this) {
                is InventoryButton -> {
                    event.isCancelled = true // Cancel the default action.
                    clickHandler(event) // Execute the button's action.
                }
                is InventoryItem -> {
                    event.isCancelled = cancelClick // Block the click if configured.
                }
            }
        }
    }

    /**
     * Handles inventory close events.
     */
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        if (event.inventory.holder != this) return
        if (event.reason != InventoryCloseEvent.Reason.PLUGIN) // Check the reason for closure.
            closeHandler(event) // Execute the close handler.
        closed = true
        inventory.clear() // Clear the inventory upon closure.
        objects.clear() // Clear all inventory objects.
        unregister() // Unregister events.
    }
}

/**
 * A class representing a button in the inventory that performs an action on click.
 */
data class InventoryButton(
    val clickHandler: InventoryClickEvent.() -> Unit
) : InventoryObject

/**
 * A class representing a regular inventory item that can block clicks.
 */
data class InventoryItem(
    val cancelClick: Boolean
) : InventoryObject

/**
 * An interface representing an inventory object.
 */
interface InventoryObject

/**
 * An interface for creating and opening menus.
 */
interface Menu {
    fun open(player: Player): InventoryMenu
}