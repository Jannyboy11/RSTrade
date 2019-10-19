package com.janboerman.rstrade.compat

import com.janboerman.rstrade.RSTrade
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ServerVersion {
    def detect(): ServerVersion = {
        val nmsVersion = Bukkit.getServer.getClass.getName.split("\\.")(3)
        nmsVersion match {
            case "v1_12_R1" => Version_1_12_R1
            case "v1_14_R1" => Version_1_14_R1
            case _ => UnknownVersion
        }
    }
}

sealed trait ServerVersion {
    def newTradeInventories(playerOne: Player, playerTwo: Player)(implicit rSTrade: RSTrade): (TradeInventory, TradeInventory)
    def newFillerStack(): ItemStack
}

case object Version_1_12_R1 extends ServerVersion {
    override def newTradeInventories(playerOne: Player, playerTwo: Player)(implicit rSTrade: RSTrade): (TradeInventory, TradeInventory) =
        v1_12_R1.inventory.BTradeInventory.make(playerOne, playerTwo)

    override def newFillerStack(): ItemStack = v1_12_R1.gui.Buttons.FillerStack
}

case object Version_1_14_R1 extends ServerVersion {
    //TODO

    override def newTradeInventories(playerOne: Player, playerTwo: Player)(implicit rSTrade: RSTrade): (TradeInventory, TradeInventory) = ???

    override def newFillerStack(): ItemStack = ???
}

case object UnknownVersion extends ServerVersion {
    override def newTradeInventories(playerOne: Player, playerTwo: Player)(implicit rSTrade: RSTrade): (TradeInventory, TradeInventory) =
        throw new UnsupportedOperationException()

    override def newFillerStack(): ItemStack =
        throw new UnsupportedOperationException();
}