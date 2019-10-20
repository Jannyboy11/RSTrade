package com.janboerman.rstrade.commands

import com.janboerman.rstrade.RSTrade
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

class TradeCommandExecutor(implicit plugin: RSTrade) extends CommandExecutor {
    override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
        sender match {
            case player : Player => args match {
                case Array(playerName, _*) => {
                    val otherPlayer = sender.getServer.getPlayerExact(playerName)
                    if (otherPlayer == null) {
                        sender.sendMessage(s"Player $playerName is not online.")
                    } else {

                        val (tradeInvOne, tradeInvTwo) = plugin.getServerVersion().newTradeInventories(player, otherPlayer)

                        player.openInventory(tradeInvOne)
                        otherPlayer.openInventory(tradeInvTwo)
                    }

                    true
                }
                case _ => false
            }

            case _ => {
                sender.sendMessage("You can only use this command as a Player.")
                true
            }
        }
    }
}
