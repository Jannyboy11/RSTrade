package xyz.janboerman.rstrade.compat.v1_12_R1.inventory

import xyz.janboerman.guilib.api.GuiInventoryHolder
import xyz.janboerman.rstrade.RSTrade
import xyz.janboerman.rstrade.compat.TradeGuiHolder

class TradeHolder(plugin: RSTrade, bukkitTradeInventory: BTradeInventory) extends GuiInventoryHolder(plugin, bukkitTradeInventory) with TradeGuiHolder {

    override def getInventory(): BTradeInventory = super.getInventory().asInstanceOf[BTradeInventory]
}
