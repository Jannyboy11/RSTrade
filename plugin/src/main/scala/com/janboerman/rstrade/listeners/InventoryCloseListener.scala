package com.janboerman.rstrade.listeners

import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.inventory.InventoryCloseEvent
import com.janboerman.rstrade.RSTrade
import com.janboerman.rstrade.extensions.InventorySeq._
import com.janboerman.rstrade.framework.TradeInventory

class InventoryCloseListener(implicit val plugin: RSTrade) extends Listener {

    @EventHandler
    def onInventoryClose(event: InventoryCloseEvent): Unit = {
        event.getInventory match {
            // if it's a TradeInventory - return the items if the trade was not accepted

            case tradeInventory: TradeInventory if !(tradeInventory.isAccepted && tradeInventory.isOtherAccepted) =>
                val player = tradeInventory.getPlayer()
                if (event.getPlayer == player) {
                    //TODO make this more generic, use the Offer typeclass such that money can also be returned to the player
                    for (item <- tradeInventory.getMyOffers if item != null) {
                        val map = player.getInventory.addItem(item)
                        if (!map.isEmpty) {
                            player.getWorld.dropItemNaturally(player.getLocation, map.get(Integer.valueOf(0)))
                        }
                    }
                }

                //commented out because when the confirm gui is open, the player got back his items already.
//            case confirmInventory: Inventory if confirmInventory.getHolder.isInstanceOf[AreYouSureGuiHolder] =>
//                val areYouSureGuiHolder = confirmInventory.getHolder.asInstanceOf[AreYouSureGuiHolder]
//
//                if (!areYouSureGuiHolder.isAccepted())
//                    areYouSureGuiHolder.myOffers.giveTo(event.getPlayer.asInstanceOf[Player])

            case _ => ()
        }
    }

}
