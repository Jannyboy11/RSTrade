package com.janboerman.rstrade.listeners

import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.player.PlayerMoveEvent
import com.janboerman.rstrade.extensions.TradePlayer._

class PlayerMoveListener extends Listener {

    @EventHandler
    def onMove(event: PlayerMoveEvent) = {
        event.getPlayer.cancelTrade()
    }

}
