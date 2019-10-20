package com.janboerman.rstrade

import com.janboerman.rstrade.commands.{AcceptTradeExecutor, DeclineTradeExecutor, RequestTradeExecutor, TradeCommandExecutor}
import com.janboerman.rstrade.compat.{ServerVersion, UnsupportedServerVersionException}
import com.janboerman.rstrade.compat.v1_12_R1.NMS_1_12_R1
import com.janboerman.rstrade.extensions.TradePlayer._
import com.janboerman.rstrade.listeners.{InventoryClickListener, InventoryCloseListener, PlayerPickupItemListener, PlayerQuitListener}
import org.bukkit.plugin.java.JavaPlugin
import net.milkbowl.vault.economy.Economy
import xyz.janboerman.guilib.api.GuiListener

import scala.jdk.javaapi.CollectionConverters.asScala

object RSTrade {
    private var instance: RSTrade = _

    private def setInstance(instance: RSTrade): Unit =
        this.instance = instance

    implicit def getInstance(): RSTrade = instance
}

class RSTrade extends JavaPlugin with TradePlugin {
    RSTrade.setInstance(this)

    private lazy implicit val instance = this
    private var serverVersion: ServerVersion = _
    private val guiListener = GuiListener.getInstance()

    lazy val economy : Option[Economy] = checkEconomy()

    private def checkEconomy(): Option[Economy] = {
        for {
            _ <- Option(getServer.getPluginManager.getPlugin("Vault"))
            economyRegistration <- Option(getServer.getServicesManager.getRegistration(classOf[Economy]))
        } yield economyRegistration.getProvider
    }

    override def onEnable(): Unit = {
        val pluginManager = getServer.getPluginManager
        detectServerVersion();

        pluginManager.registerEvents(guiListener, this)
        pluginManager.registerEvents(new InventoryClickListener(), this)
        pluginManager.registerEvents(new InventoryCloseListener(), this)
        pluginManager.registerEvents(new PlayerPickupItemListener(), this)
        pluginManager.registerEvents(new PlayerQuitListener(), this)

        getCommand("trade").setExecutor(new TradeCommandExecutor()) //TODO remove this
        getCommand("requestTrade").setExecutor(new RequestTradeExecutor())
        getCommand("acceptTrade").setExecutor(AcceptTradeExecutor)
        getCommand("declineTrade").setExecutor(DeclineTradeExecutor)
    }

    override def onDisable(): Unit = {
        for (player <- asScala(getServer.getOnlinePlayers) if player.isTrading()) {
            player.cancelTrade()
        }
    }

    override def getServerVersion(): ServerVersion = serverVersion

    private def detectServerVersion(): Unit = {
        serverVersion = getServer.getClass.getName.split("\\.")(3) match {
            case "v1_12_R1" => NMS_1_12_R1
            case _ => null
        }
        if (serverVersion == null) throw new UnsupportedServerVersionException(s"${getName} cannot run on server version ${getServer.getVersion}")
    }

}