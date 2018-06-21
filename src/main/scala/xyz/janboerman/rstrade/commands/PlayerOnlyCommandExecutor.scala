package xyz.janboerman.rstrade.commands

import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

trait PlayerOnlyCommandExecutor extends CommandExecutor {
    override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
        sender match {
            case player : Player => onCommand(player, command, label, args)
            case _ => {
                sender.sendMessage("You can only use this command as a player.")
                true
            }
        }
    }

    def onCommand(player: Player, command: Command, label: String, args: Array[String]): Boolean

}
