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
    private lazy implicit val instance = this
    RSTrade.setInstance(this)

    lazy val economy : Option[Economy] = checkEconomy()

    private def checkEconomy(): Option[Economy] = {
        for {
            _ <- Option(getServer.getPluginManager.getPlugin("Vault"))
            economyRegistration <- Option(getServer.getServicesManager.getRegistration(classOf[Economy]))
        } yield economyRegistration.getProvider
    }

    override def onEnable() = {
        val pluginManager = getServer.getPluginManager

        pluginManager.registerEvents(new InventoryClickListener(), this)

        getCommand("trade").setExecutor(TradeCommandExecutor) //TODO remove this
        getCommand("requestTrade").setExecutor(RequestTradeExecutor)
        getCommand("acceptTrade").setExecutor(AcceptTradeExecutor)
        getCommand("declineTrade").setExecutor(DeclineTradeExecutor)
    }

}
