package xyz.janboerman.rstrade.commands
import org.bukkit.command.Command
import org.bukkit.entity.Player
import xyz.janboerman.rstrade.framework.TradePlayer._

object DeclineTradeExecutor extends PlayerOnlyCommandExecutor {
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

                    player.removeTradeRequestFrom(otherPlayer);
                    player.sendMessage("You have declined " + otherPlayer.getName + "'s trade request.")
                    otherPlayer.sendMessage(player.getName + " has declined your trade request.")
                }
                true
            }
            case _ => false
        }
    }
}
