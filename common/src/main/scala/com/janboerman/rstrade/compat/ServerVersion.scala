package com.janboerman.rstrade.compat

import com.janboerman.rstrade.TradePlugin
import com.janboerman.rstrade.framework.TradeInventory
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

trait ServerVersion {
    def newTradeInventories(playerOne: Player, playerTwo: Player)(implicit plugin: TradePlugin): (TradeInventory, TradeInventory)
    def newFillerStack(): ItemStack
}

//case object Version_1_14_R1 extends ServerVersion {
//    //TODO
//
//    override def newTradeInventories(playerOne: Player, playerTwo: Player)(implicit plugin: TradePlugin): (TradeInventory, TradeInventory) = ???
//
//    override def newFillerStack(): ItemStack = ???
//}