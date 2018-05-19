package xyz.janboerman.rstrade

import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin
import xyz.janboerman.rstrade.commands.TradeCommandExecutor
import xyz.janboerman.rstrade.listeners.InventoryClickListener

class RSTrade extends JavaPlugin {
    private implicit val instance : RSTrade = this

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

        getCommand("trade").setExecutor(new TradeCommandExecutor())
    }

}
