package xyz.janboerman.rstrade.commands

import org.bukkit.command.Command
import org.bukkit.entity.Player
import xyz.janboerman.rstrade.framework.TradeRequest

object RequestTradeExecutor extends PlayerOnlyCommandExecutor {
    override def onCommand(player: Player, command: Command, label: String, args: Array[String]) = {
        args match {
            case Array(playerName, _*) => {
                val otherPlayer = player.getServer.getPlayerExact(playerName)
                if (otherPlayer == null) {
                    player.sendMessage("Player " + playerName + " is not online.")
                } else {
                    val request = TradeRequest(player, otherPlayer)
                    request.sendMessage()
                }
                true
            }
            case _ => false
        }
    }
}
