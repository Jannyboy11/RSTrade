package com.janboerman.rstrade.framework

import org.bukkit.entity.Player
import TradeRequest._
import com.janboerman.rstrade.compat.TradeInventory
import com.janboerman.rstrade.gui.AreYouSureGuiHolder

object TradePlayer {
    implicit def fromPlayer(accepter: Accepter): TradePlayer = new TradePlayer(accepter)
}

class TradePlayer(val player: Player) {
    def hasTradeRequestFrom(from: Requester): Boolean = hasRequestFrom(player, from)
    def removeTradeRequestFrom(from: Requester): Boolean = removeRequest(player, from)

    def hasTradeScreenOpen(): Boolean = Option(player.getOpenInventory).exists(_.getTopInventory.isInstanceOf[TradeInventory])
    def hasConfirmScreenOpen(): Boolean = Option(player.getOpenInventory).exists(_.getTopInventory.getHolder.isInstanceOf[AreYouSureGuiHolder])

    def isTrading(): Boolean = hasTradeScreenOpen() || hasConfirmScreenOpen()
    def isBusy(): Boolean = player.getOpenInventory != null

    def cancelTrade(): Unit = {
        if (hasTradeScreenOpen()) {
            val tradeInventory = player.getOpenInventory.getTopInventory.asInstanceOf[TradeInventory]
            tradeInventory.close()
        } else if (hasConfirmScreenOpen()) {
            val confirmGui = player.getOpenInventory.getTopInventory.getHolder.asInstanceOf[AreYouSureGuiHolder]
            confirmGui.close()
        }
    }
}
