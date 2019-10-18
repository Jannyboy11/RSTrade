package com.janboerman.rstrade.listeners

import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.{EventHandler, Listener}

import com.janboerman.rstrade.framework.TradePlayer._

class PlayerPickupItemListener extends Listener {

    @EventHandler
    def onItemPickup(event: EntityPickupItemEvent): Unit = {
        event.getEntity match {
            case player: Player => if (player.isTrading()) event.setCancelled(true) //TODO make this a configurable thing?
            case _ => ()
        }
    }

}
