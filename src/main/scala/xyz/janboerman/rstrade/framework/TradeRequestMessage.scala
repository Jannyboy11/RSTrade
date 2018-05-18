package xyz.janboerman.rstrade.framework

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.{ClickEvent, ComponentBuilder, TextComponent}
import net.md_5.bungee.api.chat.ClickEvent.Action

object TradeRequestMessage {

}

class TradeRequestMessage(tradeRequest: TradeRequest) {
    private val clickEvent = new ClickEvent(Action.RUN_COMMAND, "/accepttrade " + tradeRequest.initiator)

    private var text = new TextComponent(tradeRequest.initiator.getName() + " ")
    text.setColor(ChatColor.GOLD);

    private var secondHalf = ???



    def send(): Unit = ???

}
