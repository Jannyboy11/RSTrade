package xyz.janboerman.rstrade

import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin
import xyz.janboerman.rstrade.commands._
import xyz.janboerman.rstrade.listeners._

object RSTrade {
    private var instance: RSTrade = _

    private def setInstance(instance: RSTrade): Unit =
        this.instance = instance

    def getInstance(): RSTrade = instance
}

class RSTrade extends JavaPlugin {
    RSTrade.setInstance(this)

    private lazy implicit val instance = this

    lazy val economy : Option[Economy] = checkEconomy()

    private def checkEconomy(): Option[Economy] = {
        //TODO create extension methods for Server and ServicesManager so that they return Option instead? :D
        for {
            _ <- Option(getServer.getPluginManager.getPlugin("Vault"))
            economyRegistration <- Option(getServer.getServicesManager.getRegistration(classOf[Economy]))
        } yield economyRegistration.getProvider
    }

    override def onEnable(): Unit = {
        val pluginManager = getServer.getPluginManager

        //TODO create extension methods for PluginManager so that I don't need to pass the plugin instance?
        pluginManager.registerEvents(new InventoryClickListener(), this)
        pluginManager.registerEvents(new InventoryCloseListener(), this)
        pluginManager.registerEvents(new PlayerPickupItemListener(), this)

        getCommand("trade").setExecutor(TradeCommandExecutor) //TODO remove this
        getCommand("requestTrade").setExecutor(RequestTradeExecutor)
        getCommand("acceptTrade").setExecutor(AcceptTradeExecutor)
        getCommand("declineTrade").setExecutor(DeclineTradeExecutor)
    }

}
