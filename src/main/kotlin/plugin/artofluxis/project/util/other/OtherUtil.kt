package plugin.artofluxis.project.util.other

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.LONG
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonUnquotedLiteral
import kotlinx.serialization.json.jsonPrimitive
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import plugin.artofluxis.project.util.guis.CollectionMenu.openCollection
import plugin.artofluxis.project.util.guis.UpgradesMenu.openNormalUpgrades
import plugin.artofluxis.project.util.item
import plugin.artofluxis.project.util.key

typealias BigInteger = @Serializable(with = BigIntegerSerializer::class) java.math.BigInteger

@OptIn(ExperimentalSerializationApi::class)
private object BigIntegerSerializer : KSerializer<BigInteger> {

    override val descriptor = PrimitiveSerialDescriptor("java.math.BigInteger", LONG)

    override fun deserialize(decoder: Decoder): BigInteger =
        when (decoder) {
            is JsonDecoder -> decoder.decodeJsonElement().jsonPrimitive.content.toBigInteger()
            else -> decoder.decodeString().toBigInteger()
        }

    override fun serialize(encoder: Encoder, value: BigInteger) =
        when (encoder) {
            is JsonEncoder -> encoder.encodeJsonElement(JsonUnquotedLiteral(value.toString()))
            else -> encoder.encodeString(value.toString())
        }
}

val magnitudeKeys = listOf(
    "num.thousands",
    "num.millions",
    "num.billions",
    "num.trillions",
    "num.quadrillions",
    "num.quintillions",
    "num.sextillions",
    "num.septillions",
    "num.octillions",
    "num.nonillions",
    "num.decillions",
    "num.undecillions",
    "num.duodecillions",
    "num.tredecilions",
    "num.quattuordecillions",
    "num.quindecillions",
    "num.sexdecillions",
    "num.septendecillions",
    "num.octodecillions",
    "num.novemdecillions",
    "num.vigintillions",
    "num.unvigintillions",
    "num.duovigintillions",
    "num.tresvigintillions",
    "num.quattuorvigintillions",
    "num.quinvigintillions",
    "num.sexvigintillions",
    "num.septenvigintillions",
    "num.octovigintillions",
    "num.novemvigintillions",
    "num.trigintillions",
    "num.untrigintillions",
    "num.duotrigintillions",
    "num.trestrigintillions",
    "num.quattuortrigintillions",
    "num.quintrigintillions",
    "num.sextrigintillions",
    "num.septentrigintillions",
    "num.octotrigintillions",
    "num.novemtrigintillions",
    "num.quadragintillions",
    "num.unquadragintillions",
    "num.duoquadragintillions",
    "num.trequadragintillions",
    "num.quattuorquadragintillions",
    "num.quinquadragintillions",
    "num.sexquadragintillions",
    "num.septenquadragintillions",
    "num.octoquadragintillions",
    "num.novemquadragintillions",
    "num.quinquagintillions",
    "num.unquinquagintillions",
    "num.duoquinquagintillions",
    "num.trequinquagintillions",
    "num.quattuorquinquagintillions",
    "num.quinquinquagintillions",
    "num.sexquinquagintillions",
    "num.septenquinquagintillions",
    "num.octoquinquagintillions",
    "num.novemquinquagintillions",
    "num.sexagintillions",
    "num.unsexagintillions",
    "num.duosexagintillions",
    "num.treseхagintillions",
    "num.quattuorsexagintillions",
    "num.quinsexagintillions",
    "num.sexsexagintillions",
    "num.septensexagintillions",
    "num.octosexagintillions",
    "num.novemsexagintillions",
    "num.septuagintillions",
    "num.unseptuagintillions",
    "num.duoseptuagintillions",
    "num.treseptuagintillions",
    "num.quattuorseptuagintillions",
    "num.quinseptuagintillions",
    "num.sexseptuagintillions",
    "num.septenseptuagintillions",
    "num.octoseptuagintillions",
    "num.novemseptuagintillions",
    "num.octogintillions",
    "num.unoctogintillions",
    "num.duooctogintillions",
    "num.treoctogintillions",
    "num.quattuoroctogintillions",
    "num.quinoctogintillions",
    "num.sexoctogintillions",
    "num.septenoctogintillions",
    "num.octooctogintillions",
    "num.novemoctogintillions",
    "num.nonagintillions",
    "num.unnonagintillions",
    "num.duononagintillions",
    "num.trenonagintillions",
    "num.quattuornonagintillions",
    "num.quinnonagintillions",
    "num.sexnonagintillions",
    "num.septennonagintillions",
    "num.octononagintillions",
    "num.novemnonagintillions",
    "num.centillions"
)

@Serializable
enum class Notation {
    NORMAL, SCIENTIFIC
}

fun BigInteger.formatted(notation: Notation): Component {
    if (this < BigInteger.valueOf(10_000)) {
        return text(this.toString())
    }
    val magnitude = this.toString().length - 1

    return when (notation) {
        Notation.NORMAL -> {
            val index = (magnitude / 3).coerceAtMost(magnitudeKeys.size - 1) - 1

            if (index >= magnitudeKeys.size) return this.formatted(Notation.SCIENTIFIC)

            val value = this.toString()
            val significantDigits = magnitude % 3 + 1 // Количество значащих цифр до точки
            val formattedString = buildString {
                append(value.substring(0, significantDigits)) // Берем первые значащие цифры
                if (value.length > significantDigits) {
                    append(".")
                    append(value.substring(significantDigits, (significantDigits + 2).coerceAtMost(value.length)))
                }
            }.trimEnd('0').removeSuffix(".") // Убираем лишние нули и точку, если она осталась

            translatable(magnitudeKeys[index], text(formattedString))
        }

        Notation.SCIENTIFIC -> {
            val sciValue = this.toBigDecimal()
                .movePointLeft(magnitude)
                .setScale(2, java.math.RoundingMode.HALF_UP)
                .stripTrailingZeros()
            text("${sciValue.stripTrailingZeros()}e$magnitude")
        }
    }
}

enum class ItemAction(
    val call: (PlayerInteractEvent) -> Unit
) {
    MENU({ event ->
        val player = event.player
        openNormalUpgrades(player)
    }),
    COLLECTION({ event ->
        val player = event.player
        openCollection(player, 1)
    })
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
class ItemActionType : PersistentDataType<Integer, ItemAction> {
    override fun getPrimitiveType(): Class<Integer> {
        return Integer::class.java
    }

    override fun getComplexType(): Class<ItemAction> {
        return ItemAction::class.java
    }

    override fun toPrimitive(complex: ItemAction, context: PersistentDataAdapterContext): Integer {
        return complex.ordinal as Integer
    }

    override fun fromPrimitive(primitive: Integer, context: PersistentDataAdapterContext): ItemAction {
        return ItemAction.entries[primitive.toInt()]
    }
}


fun Player.setDefaultInventory() {
    this.inventory.setItem(1, item(Material.GOLD_INGOT) {
        this.itemName(text("ᴍᴇɴᴜ").color(MColor.GOLD))
        this.isHideTooltip = true
        this.persistentDataContainer.set(key("action"), ItemActionType(), ItemAction.MENU)
    })
    this.inventory.setItem(7, item(Material.NETHERITE_SCRAP) {
        this.itemName(text("ʙʟᴏᴄᴋ ᴄᴏʟʟᴇᴄᴛɪᴏɴ").color(MColor.GRAY))
        this.isHideTooltip = true
        this.persistentDataContainer.set(key("action"), ItemActionType(), ItemAction.COLLECTION)
    })
}