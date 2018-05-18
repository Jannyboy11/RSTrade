package xyz.janboerman.rstrade

import org.bukkit.entity.Player

package object compat {

    def newTradeInventories(playerOne : Player, playerTwo : Player) : (TradeInventory, TradeInventory) = {
        val packageString = playerOne.getServer.getClass.getPackageName
        if (!packageString.startsWith("org.bukkit.craftbukkit"))
            throw new UnsupportedServerVersionException("RSTrade only works on CraftBukkit-based servers")

        val packageVersion = packageString.split("\\.")(3)
        packageVersion match {
            case "v1_12_R1" => v1_12_R1.inventory.BTradeInventory.make(playerOne, playerTwo)
            case _ => throw new UnsupportedServerVersionException("Unsupported NMS version: " + packageVersion)
        }
    }

    //if i'm going to do nms clickable chat messages, then make a ServerVersion enum.
    //I might just depend on spigot instead and use spigot's chat api.
    //In that case, replace "CraftBukkit-based servers" with "spigot".

}
