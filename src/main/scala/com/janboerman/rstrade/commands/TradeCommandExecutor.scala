package com.janboerman.rstrade.commands

import com.janboerman.rstrade.RSTrade
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import com.janboerman.rstrade.compat.newTradeInventories

//TODO I might depend on ACF? :-)
//TODO I kinda wanna do it, just to show off it works with Scala :-)
class TradeCommandExecutor(implicit plugin: RSTrade) extends CommandExecutor {
    override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
        sender match {
            case player : Player => args match {
                case Array(playerName, _*) => {
                    val otherPlayer = sender.getServer.getPlayerExact(playerName)
                    if (otherPlayer == null) {
                        sender.sendMessage("Player " + otherPlayer + " is not online.")
                    } else {

                        //TODO check if the player can see the target player

                        //TODO send a request instead.
                        //TODO the accept method should check whether this sender has no open inventory
                        val (tradeInvOne, tradeInvTwo) = newTradeInventories(player, otherPlayer)


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
