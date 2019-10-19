package com.janboerman.rstrade.listeners

import com.janboerman.rstrade.RSTrade
import com.janboerman.rstrade.framework.TradeRequest
import com.janboerman.rstrade.extensions.TradePlayer._
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener(implicit plugin: RSTrade) extends Listener {

    @EventHandler
    def onQuit(event: PlayerQuitEvent): Unit = {
        val player = event.getPlayer

        //if a player is trading, cancel the trades
        player.cancelTrade()

        //remove incoming and outgoing requests from this player
        TradeRequest.removeAllRequests(player)
    }

}
