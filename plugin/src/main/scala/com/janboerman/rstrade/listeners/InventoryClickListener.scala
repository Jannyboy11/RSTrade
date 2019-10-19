package com.janboerman.rstrade.listeners

import org.bukkit.entity.Player
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.inventory.InventoryClickEvent
import com.janboerman.rstrade.RSTrade
import com.janboerman.rstrade.framework.TradeInventory

class InventoryClickListener(implicit val plugin : RSTrade) extends Listener {

    @EventHandler
    def onInventoryClick(event : InventoryClickEvent): Unit = {
        event.getView.getTopInventory match {
            case _ : TradeInventory => event.getWhoClicked match {
                case player : Player => player.getServer.getScheduler.runTask(plugin, new Runnable {
                    override def run(): Unit = player.updateInventory()
                })
                case _ => ()
            }
            case _ => ()
        }
    }

}
