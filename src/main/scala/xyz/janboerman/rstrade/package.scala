package xyz.janboerman

import org.bukkit.inventory.{Inventory, ItemStack}

package object rstrade {

    implicit class InventoryExtensions(inventory: Inventory) extends IndexedSeq[ItemStack] {
        override def length: Int = inventory.getSize
        override def apply(idx: Int): ItemStack = inventory.getItem(idx)
    }

}
