package com.janboerman.rstrade.commands

import com.janboerman.rstrade.RSTrade
import org.bukkit.command.Command
import org.bukkit.entity.Player
import com.janboerman.rstrade.compat.newTradeInventories
import com.janboerman.rstrade.framework.TradePlayer._

object AcceptTradeExecutor extends PlayerOnlyCommandExecutor {
    private implicit val plugin: RSTrade = RSTrade.getInstance();

    override def onCommand(player: Player, command: Command, label: String, args: Array[String]): Boolean = {
        args match {
            case Array(playerName, _*) => {
                val otherPlayer = player.getServer.getPlayerExact(playerName)
                if (otherPlayer == null) {
                    player.sendMessage("Player " + playerName + " is not online.")
                } else {
                    //check whether there was actually an trade invitation
                    if (!player.hasTradeRequestFrom(otherPlayer)) {
                        player.sendMessage("You don't have a trade request from " + otherPlayer.getName + ".")
                        return true
                    }

                    //check whether the other player doesn't have an inventory opened
                    if (otherPlayer.isBusy()) {
                        player.sendMessage(otherPlayer.getName + " is currently busy.")
                        return true
                    }

                    player.removeTradeRequestFrom(otherPlayer)

                    val (tradeInvOne, tradeInvTwo) = newTradeInventories(player, otherPlayer)
                    player.openInventory(tradeInvOne)
                    otherPlayer.openInventory(tradeInvTwo)
                }
                true
            }
            case _ => false
        }
    }
}
