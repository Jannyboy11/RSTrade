package com.janboerman.rstrade.listeners

import com.janboerman.rstrade.RSTrade
import com.janboerman.rstrade.framework.TradePlayer._
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener(implicit plugin: RSTrade) extends Listener {

    @EventHandler
    def onQuit(event: PlayerQuitEvent): Unit = {
        event.getPlayer.cancelTrade()
    }

}
