package plugin.artofluxis.project.util.other

import org.bukkit.Material
import java.math.BigInteger

val blocks = listOf(
    Material.COARSE_DIRT,
    Material.ROOTED_DIRT,
    Material.DIRT,
    Material.MYCELIUM,
    Material.GRASS_BLOCK,
    Material.MOSS_BLOCK,
    Material.GRAVEL,
    Material.ANDESITE,
    Material.DIORITE,
    Material.GRANITE,
    Material.STONE,
    Material.COAL_ORE,
    Material.COPPER_ORE,
    Material.IRON_ORE,
    Material.REDSTONE_ORE,
    Material.LAPIS_ORE,
    Material.GOLD_ORE,
    Material.DIAMOND_ORE,
    Material.EMERALD_ORE,
    Material.DEEPSLATE,
    Material.DEEPSLATE_COAL_ORE,
    Material.DEEPSLATE_COPPER_ORE,
    Material.DEEPSLATE_IRON_ORE,
    Material.DEEPSLATE_REDSTONE_ORE,
    Material.DEEPSLATE_LAPIS_ORE,
    Material.DEEPSLATE_GOLD_ORE,
    Material.DEEPSLATE_DIAMOND_ORE,
    Material.DEEPSLATE_EMERALD_ORE,
    Material.AMETHYST_BLOCK,
    Material.BUDDING_AMETHYST,
    Material.MAGMA_BLOCK
)

val blocksToEarnings = HashMap(blocks.associateWith { BigInteger("3").pow(blocks.indexOf(it)) })