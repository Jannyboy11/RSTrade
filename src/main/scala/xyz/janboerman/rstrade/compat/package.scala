package xyz.janboerman.rstrade

import org.bukkit.entity.Player

package object compat {

    def newTradeInventories(playerOne : Player, playerTwo : Player): (TradeInventory, TradeInventory) = {
        try {
            playerOne.spigot()
        } catch {
            case _: NoSuchMethodError => throw new UnsupportedServerVersionException("RSTrade only works on Spigot-based servers")
        }

        val packageString = playerOne.getServer.getClass.getPackageName

        val packageVersion = packageString.split("\\.")(3)
        packageVersion match {
            case "v1_12_R1" => v1_12_R1.inventory.BTradeInventory.make(playerOne, playerTwo)
            case _ => throw new UnsupportedServerVersionException("Unsupported NMS version: " + packageVersion)
        }
    }

}
