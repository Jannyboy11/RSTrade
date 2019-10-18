package com.janboerman.rstrade.listeners

import com.janboerman.rstrade.compat.TradeInventory
import org.bukkit.entity.Player
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.inventory.InventoryClickEvent
import com.janboerman.rstrade.RSTrade

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
