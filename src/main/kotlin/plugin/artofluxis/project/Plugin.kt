package plugin.artofluxis.project

import net.kyori.adventure.key.Key
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import net.kyori.adventure.util.UTF8ResourceBundleControl
import org.bukkit.*
import org.bukkit.plugin.java.JavaPlugin
import plugin.artofluxis.project.StatsLoad.load
import plugin.artofluxis.project.StatsLoad.save
import plugin.artofluxis.project.util.commands.NotationCommand
import plugin.artofluxis.project.util.other.*
import java.util.*
import kotlin.time.Duration.Companion.seconds

lateinit var plugin: Plugin
lateinit var plotWorld: World

class Plugin : JavaPlugin() {

    override fun onEnable() {

        val registry: TranslationRegistry = TranslationRegistry.create(Key.key("namespace:value"))

        val bundleEN = ResourceBundle.getBundle("locale.Bundle", Locale.US, UTF8ResourceBundleControl.get())
        registry.registerAll(Locale.US, bundleEN, true)

        val localeRU = Locale.of("ru", "RU")
        val bundleRU = ResourceBundle.getBundle("locale.Bundle", localeRU, UTF8ResourceBundleControl.get())
        registry.registerAll(localeRU, bundleRU, true)

        GlobalTranslator.translator().addSource(registry)

        plugin = this

        plotWorld = Bukkit.createWorld(WorldCreator("plotworld").apply {
            type(WorldType.FLAT)
            generatorSettings("""{"layers":[],"biome":"the_void"}""")
        })!!

        plotWorld.setGameRule(GameRule.MOB_GRIEFING, false)
        plotWorld.setGameRule(GameRule.RANDOM_TICK_SPEED, 0)
        plotWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        plotWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        plotWorld.setGameRule(GameRule.DO_FIRE_TICK, false)
        plotWorld.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, true) // for debug, change later -------------

//        if (!File(plugin.dataFolder, "config.yml").exists()) saveDefaultConfig()
//        loadConfig()

        Bukkit.getPluginManager().registerEvents(GlobalListener, this)
        Bukkit.getPluginManager().registerEvents(CancelListener, this)

        // plugin.getCommand("")!!.setExecutor()
        plugin.getCommand("notation")!!.setExecutor(NotationCommand)

        server.commandMap.getCommand("plugins")?.permission = "*"

        runTaskTimer(0.1.seconds) {
            for (player in Bukkit.getOnlinePlayers()) {
                val plot = player.plot ?: continue

                plot.tick()

                player.showBossBar(plot.bossBar)
                player.sendActionBar(plot.stats.money.formatted(plot.stats.notation)) // money will be moved to scoreboard eventually
            }
        }

        load()
    }

    override fun onDisable() {
        save()
    }
}
