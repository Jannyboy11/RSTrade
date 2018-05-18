package xyz.janboerman.rstrade.compat.v1_12_R1.inventory

import java.util.Objects

import net.minecraft.server.v1_12_R1._
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryView
import org.bukkit.inventory.InventoryView

import scala.collection.{JavaConverters, mutable}

object TradeContainer {
//    private val ASCII_LAYOUT = Array(
//        "    AOOOO",
//        "    XOOOO",
//        "    EOOOO",
//        "    XOOOO",
//        "    XOOOO",
//        "    DOOOO",
//    )

    //Scala, why is there no Immutable LinkedHashSet??? I don't want to use TreeSet
    private val TOP_CLICKABLE_SLOTS = new mutable.LinkedHashSet[Int]()
    for (y <- 0 until TradeInventory.SubHeigth; x <- 0 until TradeInventory.SubWidth) {
        TOP_CLICKABLE_SLOTS.add(y * TradeInventory.Width + x)
    }
}

//TODO fix so that it actually works
//TODO fix regular clicks (issue is only clientside or not?)
//TODO make sure items go back into the players' inventory (on abort)

class TradeContainer(id: Int, val viewingPlayer: EntityHuman, val tradeInventory: TradeInventory) extends Container
    with Traversable[Slot] with Iterable[Slot] with IndexedSeq[Slot] {

    this.windowId = id

//    private val myOffers = tradeInventory.myOffers
//    private val otherOffers = tradeInventory.otherOffers
//    private val buttons = tradeInventory.buttons
    private val viewing : CraftHumanEntity = viewingPlayer.getBukkitEntity
    private val playerInventory = viewingPlayer.inventory

    private lazy val bukkitInventory : BTradeInventory = new BTradeInventory(tradeInventory)
    private lazy val bukkitView = new CraftInventoryView(viewing, bukkitInventory, this) //TODO extend CraftInventoryView?

    /* initialize slots according to:
     *
     * M M M M A O O O O
     * M M M M X O O O O
     * M M M M E O O O O
     * M M M M X O O O O
     * M M M M X O O O O
     * M M M M D O O O O
     *
     * P P P P P P P P P
     * P P P P P P P P P
     * P P P P P P P P P
     * H H H H H H H H H
     *
     * where
     *  M = my offer slots
     *  O = other player offer slots
     *  A = accept button
     *  D = decline button
     *  E = vault economy
     *  P = player regular slots
     *  H = player hotbar slots
     */

    //initialize slots
    for (rowNumber <- 0 until TradeInventory.Height; columnNumber <- 0 until 9) {
        val upperSlot = calculateUpperSlot(columnNumber, rowNumber)
        nmsAddSlot(upperSlot)
    }
    for (rowNumber <- 0 until 4; columnNumber <- 0 until 9) {
        val lowerSlot = calculateLowerSlot(columnNumber, rowNumber)
        nmsAddSlot(lowerSlot)
    }

    private def calculateUpperSlot(columnNumber: Int, rowNumber: Int) : Slot = {
        if (inRange(rowNumber, 0, 6)) {
            val index = columnNumber + rowNumber * 9

            //magic numbers! :D
            val xPos = 8 + columnNumber * 18
            val yPos = 18 + rowNumber * 18

            if (inRange(columnNumber, 0, 4)) return new Slot(tradeInventory, index, xPos, yPos)
            else if (columnNumber == 4) return new InaccessibleSlot(tradeInventory, index, xPos, yPos) //TODO ButtonSlot class?
            else return new InaccessibleSlot(tradeInventory, index, xPos, yPos)
        }

        throw new IllegalArgumentException("rowNumber must be between 0 (inclusive) and 6 (exclusive), but was " + rowNumber)
    }

    private def calculateLowerSlot(columnNumber: Int, rowNumber: Int) : Slot =  {
        //lots of magic values, here I come!
        val i = (6 - 4) * 18 //(rows - 4) * 18 = 2 * 18 = 36

        if (inRange(rowNumber, 0, 3)) {
            if (!inRange(columnNumber, 0, 9))
                throw new IllegalArgumentException("columnNumber must be between 0 (inclusive) and 9 (exclusive), but was " + columnNumber)

            val index = columnNumber + rowNumber * 9 + 9

            //more magic numbers!
            val xPos = 8 + columnNumber * 18
            val yPos = 103 + rowNumber * 18 + i

            return new Slot(playerInventory, index, xPos, yPos)
        }

        else if (rowNumber == 3) return new Slot(playerInventory, columnNumber, 8 + columnNumber * 18, 161 + i)
        else throw new IllegalArgumentException("rowNumber must between 0 (inclusive) and 4 (exclusive), but was " + rowNumber)
    }

    private def inRange(n : Int, lowerBoundInclusive : Int, upperBoundExclusive: Int) : Boolean =
        lowerBoundInclusive <= n && n < upperBoundExclusive

    override def getBukkitView: InventoryView = bukkitView

    override def canUse(entityHuman: EntityHuman): Boolean = entityHuman == viewingPlayer

    override def c(entityHuman: EntityHuman): Boolean = true

    override def shiftClick(entityhuman: EntityHuman, rawIndex: Int): ItemStack = {

        /*
         *  algorithm description:
         *      if top slot clicked
         *          if my offers clicked
         *              transfer to bottom
         *          else (buttom slot was clicked)
         *              transfer to top, to my offers only
         */
        val clickedSlot = slots.get(rawIndex)
        if (clickedSlot != null && clickedSlot.hasItem) {

            val clickedItemStack = clickedSlot.getItem
            val clickedItemStackClone = clickedItemStack.cloneItemStack

            if (rawIndex < TradeInventory.Height * TradeInventory.Height) {
                //top inventory was clicked

                if (TradeContainer.TOP_CLICKABLE_SLOTS.contains(rawIndex)) {
                    val topInventorySize = tradeInventory.getSize
                    val topWasClicked = true
                    //click to towards
                    if (!a(clickedItemStack, topInventorySize, slots.size(), topWasClicked)) return ItemStack.a
                } else {
                    entityhuman match {
                        case entityPlayer: EntityPlayer => entityPlayer.updateInventory(this)
                    }
                    return ItemStack.a
                }
            } else {
                //bottom inventory was clicked

                var somethingTransferred = false

                val topSlotIterator = TradeContainer.TOP_CLICKABLE_SLOTS.iterator
                while (topSlotIterator.hasNext && !clickedItemStack.isEmpty) {
                    //while there are slots the stack can merge into, and while the clicked stack is not empty

                    val topSlotIndex = topSlotIterator.next()
                    val topSlot = slots.get(topSlotIndex)

                    val topSlotStack = topSlot.getItem

                    if (topSlotStack.isEmpty) {
                        //top slot is empty - move as much as possible.

                        val amountTransfer = topSlot.getMaxStackSize(clickedItemStack)
                        if ({somethingTransferred = amountTransfer > 0; somethingTransferred}) {
                            topSlot.set(clickedItemStack.cloneAndSubtract(amountTransfer))
                            topSlot.f()
                        }

                    } else {
                        //subtract from the clicked itemstack and merge into the clickable slots

                        //this is likely gonna change when 1.13 arrives
                        var canMerge = topSlotStack.getCount < topSlot.getMaxStackSize(topSlotStack)
                        canMerge &= (clickedItemStack.getItem eq topSlotStack.getItem)
                        if (clickedItemStack.usesData) canMerge &= (clickedItemStack.getData == topSlotStack.getData)
                        canMerge &= Objects.equals(clickedItemStack.getTag, topSlotStack.getTag)

                        if (canMerge) {
                            val amountTransfer = Math.min(clickedItemStack.getCount, topSlot.getMaxStackSize(clickedItemStack) - topSlotStack.getCount)

                            topSlotStack.add(amountTransfer)
                            clickedItemStack.subtract(amountTransfer)

                            topSlot.f() //updates inventory
                            somethingTransferred = true
                        }
                    }
                }

                if (!somethingTransferred) {
                    entityhuman match {
                        case entityPlayer: EntityPlayer => entityPlayer.updateInventory(this)
                    }

                    return ItemStack.a
                }
            }

            if (clickedSlot.hasItem) {
                clickedSlot.f() //update inventory
            }  else {
                clickedSlot.set(ItemStack.a)
            }

            entityhuman match {
                case entityPlayer: EntityPlayer => entityPlayer.updateInventory(this)
            }

            return clickedItemStackClone
        }

        return ItemStack.a //no shiftclicking allowed
    }


    private def nmsAddSlot(slot: Slot) : Unit = a(slot)


    // Scala traits

    override def foreach[U](f: Slot => U) : Unit = for(slot <- iterator()) f(slot)

    override def iterator() : Iterator[Slot] = JavaConverters.asScalaIterator(slots.iterator())

    override def apply(index : Int) : Slot = slots.get(index)

    override def length() : Int = slots.size()
}
