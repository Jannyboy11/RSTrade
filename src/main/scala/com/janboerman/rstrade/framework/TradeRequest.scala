package com.janboerman.rstrade.framework

import com.janboerman.rstrade.RSTrade
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.{ClickEvent, TextComponent}
import net.md_5.bungee.api.chat.ClickEvent.Action
import org.bukkit.entity.{HumanEntity, Player}
import org.bukkit.event.{EventPriority, HandlerList, Listener}
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitTask

import scala.collection.mutable.Map
import scala.collection.mutable.HashMap

object TradeRequest {
    type Requester = Player
    type Accepter = Player

    private val incomingTradeRequests = new HashMap[Accepter, Map[Requester, TradeRequest]]()
    private val outgoingTradeRequests = new HashMap[Requester, Map[Accepter, TradeRequest]]()

    private val plugin = RSTrade.getInstance
    private val server = plugin.getServer
    private val pluginManager = server.getPluginManager
    private val listener = new Listener() {}

    pluginManager.registerEvent(classOf[PlayerQuitEvent], listener, EventPriority.NORMAL, (_, event) => {
        val playerQuitEvent = event.asInstanceOf[PlayerQuitEvent]
        val quittingPlayer = playerQuitEvent.getPlayer

        incomingTradeRequests.remove(quittingPlayer)
        outgoingTradeRequests.remove(quittingPlayer)
    }, plugin)


    def hasRequestFrom(target: Accepter, from: Requester): Boolean =
        incomingTradeRequests.get(target).exists(_ contains from)

    private def removeRequest(tradeRequest: TradeRequest): Boolean = {
        val incoming = incomingTradeRequests.get(tradeRequest.target)
        val outgoing = outgoingTradeRequests.get(tradeRequest.initiator)

        (incoming, outgoing) match {
            case (Some(initiators), Some(targets)) if initiators.get(tradeRequest.initiator) == targets.get(tradeRequest.target) =>
                initiators.remove(tradeRequest.initiator)
                initiators.remove(tradeRequest.target)
                true
            case _ => false
        }
    }

    def removeRequest(accepter: Accepter, requester: Requester): Boolean =
        incomingTradeRequests.get(accepter).exists(innerMap => innerMap.remove(requester) match {
            case None => false
            case Some(tradeRequest) =>
                removeRequest(tradeRequest)
                if (innerMap.isEmpty) incomingTradeRequests.remove(accepter)
                true
        })

    def apply(initiator: Requester, target: Accepter): TradeRequest = {
        val incomingInnerMap = incomingTradeRequests.getOrElseUpdate(target, new HashMap[Requester, TradeRequest]())
        val outgoingInnerMap = outgoingTradeRequests.getOrElseUpdate(initiator, new HashMap[Accepter, TradeRequest]())

        (incomingInnerMap.get(initiator), outgoingInnerMap.get(target)) match {
            //use existing request if it exists
            case (Some(x), Some(y)) if x.eq(y) =>
                x.resetTimeOut()
                return x //returns from the method!
            case _ => ();
        }

        //create a new trade request and register
        val tradeRequest = new TradeRequest(initiator, target, incomingInnerMap, outgoingInnerMap)
        incomingInnerMap.put(initiator, tradeRequest)
        outgoingInnerMap.put(target, tradeRequest)
        //the tradeRequest will unregister itself after 60 seconds.

        //the tradeRequest will be unregistered when either the initiator or the target leaves the server
        val listener = new Listener() {}
        pluginManager.registerEvent(classOf[PlayerQuitEvent], listener, EventPriority.NORMAL, (_, event) => {
            val quittingPlayer = event.asInstanceOf[PlayerQuitEvent].getPlayer

            if (quittingPlayer == initiator) {
                incomingInnerMap.get(initiator).filter(_ eq tradeRequest).foreach(_ => incomingInnerMap remove initiator)
                HandlerList.unregisterAll(listener)
            } else if (quittingPlayer == target) {
                outgoingInnerMap.get(target).filter(_ eq tradeRequest).foreach(_ => outgoingInnerMap remove target)
                HandlerList.unregisterAll(listener)
            }
        }, plugin)

        return tradeRequest
    }

    def unapply(arg: TradeRequest): Option[(Requester, Accepter)] = Some(arg.initiator, arg.target)
}
import TradeRequest.{Requester, Accepter}

class TradeRequest private(val initiator: Requester,
                           val target: Accepter,
                           private val incomingMap: Map[Requester, TradeRequest],
                           private val outgoingMap: Map[Accepter, TradeRequest]) {

    private val plugin = RSTrade.getInstance()
    private val server = plugin.getServer
    private val pluginManager = server.getPluginManager
    private val scheduler = server.getScheduler

    private var runningTimeoutTask: BukkitTask = newTimeoutTask()

    def newTimeoutTask(): BukkitTask =
        scheduler.runTaskLater(plugin, () => unregister(), 20L * 60)

    def resetTimeOut(): Unit = {
        if (runningTimeoutTask != null) runningTimeoutTask.cancel()
        runningTimeoutTask = newTimeoutTask()
    }

    private def unregister(): Unit = {
        incomingMap.get(initiator).filter(_.eq(this)).foreach(_ => incomingMap.remove(initiator))
        outgoingMap.get(target).filter(_.eq(this)).foreach(_ => outgoingMap.remove(target))
        runningTimeoutTask.cancel()
    }

    def sendMessage(): Unit = {
        val clickEvent = new ClickEvent(Action.RUN_COMMAND, "/acceptTrade " + initiator.getName)
        val invitation = new TextComponent(initiator.getName() + " ")
        invitation.setColor(ChatColor.GOLD)
        invitation.setClickEvent(clickEvent)
        val invitationSecondHalf = new TextComponent("would like to trade with you! Click here to accept!")
        invitationSecondHalf.setColor(ChatColor.YELLOW)
        invitation.setClickEvent(clickEvent)
        invitation.addExtra(invitationSecondHalf)

        val confirmationMessage = new TextComponent("A trade request was sent to ");
        confirmationMessage.setColor(ChatColor.YELLOW)
        confirmationMessage.addExtra({
            val textComponent = new TextComponent(target.getName)
            textComponent.setColor(ChatColor.GOLD)
            textComponent
        })
        confirmationMessage.addExtra({
            val textComponent = new TextComponent(".")
            textComponent.setColor(ChatColor.YELLOW)
            textComponent
        })

        target.spigot().sendMessage(invitation)
        initiator.spigot().sendMessage(confirmationMessage)
    }
}

