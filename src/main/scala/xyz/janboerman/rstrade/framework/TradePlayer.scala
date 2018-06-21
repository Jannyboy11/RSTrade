package xyz.janboerman.rstrade.framework

import org.bukkit.entity.Player
import xyz.janboerman.rstrade.compat.TradeInventory
import xyz.janboerman.rstrade.framework.TradeRequest._

object TradePlayer {
    implicit def fromPlayer(accepter: Accepter): TradePlayer = new TradePlayer(accepter)
}

class TradePlayer(val player: Player) {
    def hasTradeRequestFrom(from: Requester): Boolean = hasRequestFrom(player, from)

    def removeTradeRequestFrom(from: Requester): Boolean = removeRequest(player, from)

    def isTrading(): Boolean = Option(player.getOpenInventory).exists(_.getTopInventory.isInstanceOf[TradeInventory])

    def isBusy(): Boolean = player.getOpenInventory != null
}
