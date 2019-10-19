package com.janboerman.rstrade.compat.v1_12_R1.inventory

import com.janboerman.rstrade.gui.TradeGuiHolder
import com.janboerman.rstrade.{TradePlugin, framework}
import net.minecraft.server.v1_12_R1.InventorySubcontainer
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventory
import org.bukkit.entity.Player

object BTradeInventory {

    def make(playerOne: Player, playerTwo: Player)(implicit plugin: TradePlugin): (BTradeInventory, BTradeInventory) = {
        val offersOne = new InventorySubcontainer(/*custom name*/null, /*hasCustomName*/false, TradeInventory.SubSize, /*InventoryHolder*/playerOne)
        val offersTwo = new InventorySubcontainer(/*custom name*/null, /*hasCustomName*/false, TradeInventory.SubSize, /*InventoryHolder*/playerTwo)

        val buttonsOne = new InventorySubcontainer(null, false, TradeInventory.SubHeigth, playerOne)
        val buttonsTwo = new InventorySubcontainer(null, false, TradeInventory.SubHeigth, playerTwo)

        val nmsInvOne = new TradeInventory("Trading with " + playerTwo.getName, /*inventory holder will be provided later*/null, offersOne, offersTwo, buttonsOne)
        val nmsInvTwo = new TradeInventory("Trading with " + playerOne.getName, /*inventory holder will be provided later*/null, offersTwo, offersOne, buttonsTwo)

        val bukkitInvOne = nmsInvOne.bukkitInventory
        val bukkitInvTwo = nmsInvTwo.bukkitInventory

        bukkitInvOne.setOtherInventory(bukkitInvTwo)
        bukkitInvTwo.setOtherInventory(bukkitInvOne)

        nmsInvOne.tradeInventoryHolder = new TradeGuiHolder(bukkitInvOne)
        nmsInvTwo.tradeInventoryHolder = new TradeGuiHolder(bukkitInvTwo)

        (bukkitInvOne, bukkitInvTwo)
    }
}

class BTradeInventory private[inventory] (nmsTradeInventory : TradeInventory) extends CraftInventory(nmsTradeInventory)
    with framework.TradeInventory {

    private lazy val bukkitMyOffers = new CraftInventory(nmsTradeInventory.myOffers)
    private lazy val bukkitOtherOffers = new CraftInventory(nmsTradeInventory.otherOffers)
    private lazy val bukkitButtons = new CraftInventory(nmsTradeInventory.buttons)

    override def getInventory: TradeInventory = super.getInventory.asInstanceOf[TradeInventory]

    override def getMyOffers() = bukkitMyOffers
    override def getOtherOffers() = bukkitOtherOffers
    override def getButtons() = bukkitButtons

}
