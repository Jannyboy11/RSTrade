package xyz.janboerman.rstrade.compat.v1_12_R1

import net.minecraft.server.v1_12_R1.{IInventory, ItemStack}

package object inventory {

    implicit class InventoryExtensions(inventory: IInventory) extends IndexedSeq[ItemStack] {
        override def length: Int = inventory.getSize
        override def apply(idx: Int): ItemStack = inventory.getItem(idx)
    }

}
