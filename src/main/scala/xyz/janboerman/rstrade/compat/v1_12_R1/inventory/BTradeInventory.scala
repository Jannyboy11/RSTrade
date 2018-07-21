package xyz.janboerman.rstrade.compat.v1_12_R1.inventory

import net.minecraft.server.v1_12_R1.InventorySubcontainer
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventory
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.janboerman.rstrade.RSTrade

object BTradeInventory {

    //TODO use different button inventories
    //TODO fill those with items (cause they can change?)
    //TODO if they don't change, i can re-use a single one. TODO they actually will change #Sad
    private val buttons = new InventorySubcontainer(null, false, TradeInventory.SubHeigth)

    def make(playerOne : Player, playerTwo : Player) : (BTradeInventory, BTradeInventory) = {
        val offersOne = new InventorySubcontainer(/*custom name*/null, /*hasCustomName*/false, TradeInventory.SubSize, /*InventoryHolder*/playerOne)
        val offersTwo = new InventorySubcontainer(/*custom name*/null, /*hasCustomName*/false, TradeInventory.SubSize, /*InventoryHolder*/playerTwo)

        val nmsInvOne = new TradeInventory("Trading with " + playerTwo.getName, /*inventoyr holder will be provided later*/null, offersOne, offersTwo, buttons)
        val nmsInvTwo = new TradeInventory("Trading with " + playerOne.getName, /*inventoyr holder will be provided later*/null, offersTwo, offersOne, buttons)

        val bukkitInvOne = nmsInvOne.bukkitInventory
        val bukkitInvTwo = nmsInvTwo.bukkitInventory

        nmsInvOne.tradeInventoryHolder = new TradeHolder(RSTrade.getInstance(), bukkitInvOne)
        nmsInvTwo.tradeInventoryHolder = new TradeHolder(RSTrade.getInstance(), bukkitInvTwo)

        (bukkitInvOne, bukkitInvTwo)
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

    override def foreach[U](f: ItemStack => U): Unit = for (itemStack <- getContents) f(itemStack)
}
