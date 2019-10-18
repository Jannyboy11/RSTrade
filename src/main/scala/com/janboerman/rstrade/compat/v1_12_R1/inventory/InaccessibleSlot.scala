package com.janboerman.rstrade.compat.v1_12_R1.inventory

import net.minecraft.server.v1_12_R1.{EntityHuman, IInventory, ItemStack, Slot}

class InaccessibleSlot(inventory: IInventory, index: Int, xPos: Int, yPos: Int) extends Slot(inventory, index, xPos, yPos) {

    override def isAllowed(itemStack: ItemStack) = false
    override def isAllowed(entityHuman: EntityHuman) = false
    override def set(itemStack: ItemStack): Unit = updateInventory()

    override def getMaxStackSize: Int = 0                           //needed?
    override def getMaxStackSize(itemStack: ItemStack): Int = 0     //needed?

    private def updateInventory() = f()

}
