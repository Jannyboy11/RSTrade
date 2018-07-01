package xyz.janboerman.rstrade.listeners

import org.bukkit.entity.Player
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.janboerman.rstrade.RSTrade
import xyz.janboerman.rstrade.compat.TradeInventory

class InventoryClickListener(implicit val plugin : RSTrade) extends Listener {

    @EventHandler
    def onInventoryClick(event : InventoryClickEvent): Unit = {
        event.getView.getTopInventory match {
            case _ : TradeInventory => event.getWhoClicked match {
                case player : Player => player.getServer.getScheduler.runTask(plugin, () => player.updateInventory())
                case _ => ()
            }
            case _ => ()
        }
    }

}
