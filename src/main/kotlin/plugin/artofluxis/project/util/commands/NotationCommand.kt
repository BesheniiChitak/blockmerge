package plugin.artofluxis.project.util.commands

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import plugin.artofluxis.project.util.other.*

object NotationCommand : CommandExecutor, TabCompleter {

    private val notations = Notation.entries.map { it.name }.toMutableList()

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        val notation = args.getOrNull(0) ?: return false

        if (notation !in notations || sender !is Player || !sender.hasPlot()) {
            sender.sendMessage(translatable("command.error.cant_execute_command").color(MColor.RED))
            return false
        }
        if (sender.plot!!.stats.notation.name == notation) {
            sender.sendMessage(translatable("command.error.notation_already_set").color(MColor.RED))
            return true
        }

        sender.plot!!.stats.notation = Notation.valueOf(notation)
        sender.sendMessage(translatable("command.result.notation_set", text(notation)).color(MColor.GOLD))
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String> {
        return when (args?.size) {
            1 -> notations
            else -> mutableListOf()
        }
    }
}

