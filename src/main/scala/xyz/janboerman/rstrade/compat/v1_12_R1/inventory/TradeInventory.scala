package xyz.janboerman.rstrade.compat.v1_12_R1.inventory

import java.util

import net.minecraft.server.v1_12_R1._
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.InventoryHolder
import xyz.janboerman.rstrade.compat.v1_12_R1.inventory.TradeInventory._

import scala.collection.JavaConverters

object TradeInventory {
    type Index = Int
    type RowNumber = Index
    type ColumnNumber = Index

    val MaxStackSize = 64
    val Width = 9
    val Height = 6
    val Size = Width * Height

    val SubWidth = 4
    val SubHeigth = 6
    val SubSize = SubHeigth * SubWidth
    val SplitColumn = 4

    def break(index : Index) : (ColumnNumber, RowNumber) = {
        val columnNumber = index % 9
        val rowNumber = index / 9
        return (columnNumber, rowNumber);
    }
}

class TradeInventory(val name : String, val inventoryHolder: InventoryHolder,
                     val myOffers : IInventory, val otherOffers : IInventory, val buttons : IInventory)
    extends IInventory with ITileEntityContainer
    with Traversable[ItemStack] with Iterable[ItemStack] with IndexedSeq[ItemStack] {

    lazy val bukkitInventory = new BTradeInventory(this)

    // Handy helper method
    private def decideWhichInventory(index : Index) : (IInventory, Index) = {
        val (columnNumber, rowNumber) = break(index)

        if (columnNumber < SplitColumn) (myOffers, rowNumber * SubWidth + columnNumber)
        else if (columnNumber == SplitColumn) (buttons, rowNumber)
        else (otherOffers, rowNumber * SubWidth + (columnNumber - 1 - SubWidth))
    }

    // Bukkit Stuff

    private val transaction = new util.ArrayList[HumanEntity]()
    private var maxStackSize = MaxStackSize;

    override def getMaxStackSize: Int = maxStackSize

    override def getOwner: InventoryHolder = inventoryHolder

    override def getLocation: Location = null

    override def setMaxStackSize(maxStackSize : Int): Unit = this.maxStackSize = maxStackSize

    //NMS Stuff

    // Container

    override def createContainer(playerInventory: PlayerInventory, human: EntityHuman): TradeContainer =
        new TradeContainer(human.asInstanceOf[EntityPlayer].nextContainerCounter(), human, this)

    override def getContainerName = "minecraft:container"

    // IInventory

    override def getItem(index: Int): ItemStack = {
        val (whichInventory, newIndex) = decideWhichInventory(index)
        whichInventory.getItem(newIndex)
    }

    override def startOpen(entityHuman: EntityHuman): Unit = {
        myOffers.startOpen(entityHuman)
        otherOffers.startOpen(entityHuman)
        buttons.startOpen(entityHuman)
    }

    override def update(): Unit = {
        myOffers.update()
        otherOffers.update()
        buttons.update()
    }

    override def closeContainer(entityHuman: EntityHuman): Unit = {
        myOffers.closeContainer(entityHuman)
        otherOffers.closeContainer(entityHuman)
        buttons.closeContainer(entityHuman)
    }

    override def onClose(craftHumanEntity: CraftHumanEntity): Unit = {
        myOffers.onClose(craftHumanEntity)
        otherOffers.onClose(craftHumanEntity)
        buttons.onClose(craftHumanEntity)
        transaction.remove(craftHumanEntity)
    }

    override def getViewers: util.List[HumanEntity] = transaction

    override def onOpen(craftHumanEntity: CraftHumanEntity): Unit = {
        myOffers.onOpen(craftHumanEntity)
        otherOffers.onOpen(craftHumanEntity)
        buttons.onOpen(craftHumanEntity)
        transaction.add(craftHumanEntity)
    }

    override def getContents: util.List[ItemStack] = {
        val list = new util.ArrayList[ItemStack](getSize)
        for (index <- 0 until getSize) list.add(getItem(index))
        list
    }

    override def splitStack(index: Int, amount: Int): ItemStack = {
        val (inventory, newIndex) = decideWhichInventory(index)
        inventory.splitStack(newIndex, amount)
    }

    override def setItem(index: Int, itemStack: ItemStack): Unit = {
        val (inventory, newIndex) = decideWhichInventory(index)
        inventory.setItem(newIndex, itemStack)
    }

    override def a(entityHuman: EntityHuman): Boolean = //TODO? I might just want to check the myOffers
        myOffers.a(entityHuman) && otherOffers.a(entityHuman) && buttons.a(entityHuman)


    override def b(index: Int, itemStack: ItemStack): Boolean = { //not sure what this does
        val (inventory, newIndex) = decideWhichInventory(index)
        inventory == myOffers && myOffers.b(newIndex, itemStack)
    }

    override def getProperty(property: Int): Int = 0

    /**
      * Checks whether this inventory is empty.
      * @return false, since there are always buttons in the inventory
      */
    override def x_(): Boolean = myOffers.x_ && buttons.x_ && otherOffers.x_ //isEmpty

    override def clear(): Unit = myOffers.clear()

    override def h(): Int = 0 //number of properties

    override def getSize: Int = Size

    override def setProperty(property: Int, value: Int): Unit = ()

    override def splitWithoutUpdate(index: Int): ItemStack = {
        val (inventory, newIndex) = decideWhichInventory(index)
        inventory.splitWithoutUpdate(newIndex)
    }

    override def getName: String = name

    override def hasCustomName: Boolean = name != null

    override def getScoreboardDisplayName: IChatBaseComponent =
        if (hasCustomName) new ChatComponentText(getName) else new ChatMessage(null)


    //Scala Traits

    override def foreach[U](f: ItemStack => U): Unit = for (itemStack <- iterator()) f(itemStack)

    override def iterator(): Iterator[ItemStack] = JavaConverters.asScalaIterator(getContents.iterator())

    override def apply(index : Int): ItemStack = getItem(index)

    override def length(): Int = getSize
}