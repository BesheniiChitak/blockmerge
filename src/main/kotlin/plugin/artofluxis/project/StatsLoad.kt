package plugin.artofluxis.project

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import plugin.artofluxis.project.util.other.Plot
import plugin.artofluxis.project.util.other.plots
import java.io.File

object StatsLoad {

    private val statsfolder = File(plugin.dataFolder, "variables")
    private val pluginfolder = plugin.dataFolder

    private val settingsFile = File(statsfolder, "plots.json")

    fun load() {
        if (!pluginfolder.exists()) pluginfolder.mkdirs()
        if (!statsfolder.exists()) statsfolder.mkdirs()
        else {
            if (!settingsFile.exists()) settingsFile.createNewFile()
            plots = Json.decodeFromString<HashMap<Int, Plot>>(settingsFile.readText())
        }
    }

    fun save() {
        settingsFile.writeText(Json.encodeToString(plots))
    }
}