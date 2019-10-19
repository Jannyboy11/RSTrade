package com.janboerman.rstrade.commands

import com.janboerman.rstrade.RSTrade
import com.janboerman.rstrade.framework.TradeRequest
import org.bukkit.command.Command
import org.bukkit.entity.Player

class RequestTradeExecutor(implicit val plugin: RSTrade) extends PlayerOnlyCommandExecutor {
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
