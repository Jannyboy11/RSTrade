package xyz.janboerman.rstrade.listeners

import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.inventory.InventoryCloseEvent
import xyz.janboerman.rstrade.RSTrade
import xyz.janboerman.rstrade.compat.TradeInventory

class InventoryCloseListener(implicit val plugin: RSTrade) extends Listener {

    @EventHandler
    def onInventoryClose(event: InventoryCloseEvent): Unit = {
        event.getInventory match {
            case tradeInventory: TradeInventory =>
                //TODO cancel the trade

                //TODO make all items return to players' inventories just like with the crafting table

                ()
            case _ => ()
        }
    }

}
