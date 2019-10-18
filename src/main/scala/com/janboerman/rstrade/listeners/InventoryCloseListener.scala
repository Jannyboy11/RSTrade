package com.janboerman.rstrade.listeners

import com.janboerman.rstrade._
import com.janboerman.rstrade.compat.TradeInventory
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.inventory.InventoryCloseEvent
import com.janboerman.rstrade.RSTrade

class InventoryCloseListener(implicit val plugin: RSTrade) extends Listener {

    @EventHandler
    def onInventoryClose(event: InventoryCloseEvent): Unit = {
        event.getInventory match {
            // if it's a TradeInventory - return the items
            // this should happen regardless of whether the trade was cancelled!
            case tradeInventory: TradeInventory =>
                val player = tradeInventory.getPlayer()
                if (event.getPlayer == player) {
                    for (item <- tradeInventory.getMyOffers if item != null) {
                        val map = player.getInventory.addItem(item)
                        if (!map.isEmpty) {
                            player.getWorld.dropItemNaturally(player.getLocation, map.get(Integer.valueOf(0)))
                        }
                    }
                }

            case _ => ()
        }
    }

}
