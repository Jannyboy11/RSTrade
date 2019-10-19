package com.janboerman.rstrade.extensions

import org.bukkit.inventory.{Inventory, ItemStack}

import scala.jdk.javaapi.CollectionConverters.asScala

object InventorySeq {
    implicit def fromInventory(inventory: Inventory): InventorySeq = new InventorySeq(inventory)
}

class InventorySeq(inventory: Inventory) extends IndexedSeq[ItemStack] {
    override def length: Int = inventory.getSize
    override def apply(idx: Int): ItemStack = inventory.getItem(idx)
    override def iterator: Iterator[ItemStack] = asScala(inventory.iterator)
}
