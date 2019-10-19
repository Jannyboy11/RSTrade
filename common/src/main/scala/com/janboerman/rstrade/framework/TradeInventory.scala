package com.janboerman.rstrade.framework

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import com.janboerman.rstrade.extensions.TradePlayer._

trait TradeInventory extends Inventory {
    var isAccepted = false

    private var otherInventory: TradeInventory = _
    protected def setOtherInventory(other: TradeInventory): Unit = otherInventory = other

    def isOtherAccepted: Boolean = otherInventory.isAccepted

    def getMyOffers: Inventory
    def getOtherOffers: Inventory
    def getButtons: Inventory

    def getPlayer(): Player = getMyOffers.getHolder.asInstanceOf[Player]
    def getOtherPlayer(): Player = getOtherOffers.getHolder.asInstanceOf[Player]

    def close(): Unit = {
        if (getPlayer().isTrading()) getPlayer().getOpenInventory.close()
        if (getOtherPlayer().isTrading()) getOtherPlayer().getOpenInventory.close()
    }

}
