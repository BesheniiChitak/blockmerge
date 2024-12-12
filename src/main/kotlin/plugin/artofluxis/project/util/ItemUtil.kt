package plugin.artofluxis.project.util

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import plugin.artofluxis.project.plugin
import java.net.URI
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


// Функция для создания ItemStack с заданным материалом и количеством
fun item(material: Material, amount: Int = 1) = ItemStack(material, amount)

// Функция для создания ItemStack с возможностью редактирования мета-данных (ItemMeta)
fun item(material: Material, amount: Int = 1, editMeta: ItemMeta.() -> Unit): ItemStack =
    ItemStack(material, amount).apply {
        itemMeta = itemMeta?.apply(editMeta) // Применяем изменения мета-данных, если они есть
    }

// Функция для редактирования уже существующего ItemStack, также с возможностью редактирования мета-данных
fun item(item: ItemStack, editMeta: ItemMeta.() -> Unit): ItemStack =
    item.clone().apply { // Клонируем ItemStack, чтобы не изменять оригинал
        itemMeta = itemMeta?.apply(editMeta) // Применяем изменения мета-данных
    }

fun createHead(urlString: String): ItemStack { // doesnt work, idk why ------------
    val url = URI(urlString).toURL()
    val uuid = UUID.randomUUID()
    val profile = Bukkit.createProfile(uuid)
    profile.textures.skin = url

    return item(Material.PLAYER_HEAD) {
        (this as SkullMeta).playerProfile = profile
    }
}

// Используем контракт Kotlin для проверки на null или "воздух" (отсутствие предмета)
@ExperimentalContracts
fun ItemStack?.isNullOrAir(): Boolean {
    contract { // Контракт позволяет компилятору делать выводы о типах на основе результата
        returns(false) implies (this@isNullOrAir is ItemStack)
    }
    // Возвращаем true, если ItemStack равен null или материал равен AIR (или пустой)
    return this == null || type.isAir
}

// Функция для создания уникального NamespacedKey с использованием плагина
fun key(key: String): NamespacedKey = NamespacedKey(plugin, key)
