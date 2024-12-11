package plugin.artofluxis.project.util.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object ExampleCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String> {
        return mutableListOf()
    }
}

