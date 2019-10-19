package com.janboerman.rstrade.compat.v1_12_R1

import com.janboerman.rstrade.TradePlugin
import com.janboerman.rstrade.compat.ServerVersion
import com.janboerman.rstrade.framework.TradeInventory
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Version_nms1_12_R1 extends ServerVersion {
    override def newTradeInventories(playerOne: Player, playerTwo: Player)(implicit rSTrade: TradePlugin): (TradeInventory, TradeInventory) =
        inventory.BTradeInventory.make(playerOne, playerTwo)

    override def newFillerStack(): ItemStack = gui.Buttons.FillerStack
}
