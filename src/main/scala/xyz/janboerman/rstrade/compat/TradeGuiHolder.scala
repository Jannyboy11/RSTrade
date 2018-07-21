package xyz.janboerman.rstrade.compat

import xyz.janboerman.guilib.api.GuiInventoryHolder
import xyz.janboerman.rstrade.RSTrade

//TODO move to an API package?
trait TradeGuiHolder extends GuiInventoryHolder[RSTrade] {
    override def getInventory(): TradeInventory = super.getInventory.asInstanceOf[TradeInventory]
}
