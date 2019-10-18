package com.janboerman

import org.bukkit.inventory.{Inventory, ItemStack}
import scala.jdk.javaapi.CollectionConverters.asScala

package object rstrade {

    implicit class InventoryExtensions(inventory: Inventory) extends IndexedSeq[ItemStack] {
        override def length: Int = inventory.getSize
        override def apply(idx: Int): ItemStack = inventory.getItem(idx)
        override def iterator: Iterator[ItemStack] = asScala(inventory.iterator)
    }

}
