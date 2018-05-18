package xyz.janboerman.rstrade.compat.v1_12_R1.inventory

import net.minecraft.server.v1_12_R1.InventorySubcontainer
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventory
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object BTradeInventory {

    //TODO use different button inventories
    //TODO fill those with items (cause they can change?)
    //TODO if they don't change, i can re-use a single one. TODO they actually will change #Sad
    private val buttons = new InventorySubcontainer(null, false, TradeInventory.SubHeigth)

    def make(playerOne : Player, playerTwo : Player) : (BTradeInventory, BTradeInventory) = {
        val offersOne = new InventorySubcontainer(null, false, TradeInventory.SubHeigth * TradeInventory.SubWidth, playerOne)
        val offersTwo = new InventorySubcontainer(null, false, TradeInventory.SubHeigth * TradeInventory.SubWidth, playerTwo)

        val nmsInvOne = new TradeInventory("Trading with " + playerTwo.getName, playerOne, offersOne, offersTwo, buttons)
        val nmsInvTwo = new TradeInventory("Trading with " + playerOne.getName, playerTwo, offersTwo, offersOne, buttons)

        (nmsInvOne.bukkitInventory, nmsInvTwo.bukkitInventory)
    }
}

class BTradeInventory private[inventory] (nmsTradeInventory : TradeInventory) extends CraftInventory(nmsTradeInventory)
    with xyz.janboerman.rstrade.compat.TradeInventory with Traversable[ItemStack] {

    private lazy val bukkitMyOffers = new CraftInventory(nmsTradeInventory.myOffers)
    private lazy val bukkitOtherOffers = new CraftInventory(nmsTradeInventory.otherOffers)
    private lazy val bukkitButtons = new CraftInventory(nmsTradeInventory.buttons)

    override def getInventory: TradeInventory = super.getInventory.asInstanceOf[TradeInventory]

    override def getMyOffers() = bukkitMyOffers
    override def getOtherOffers() = bukkitOtherOffers
    override def getButtons() = bukkitButtons

    override def foreach[U](f: ItemStack => U) = for (itemStack <- getContents) f(itemStack)
}
