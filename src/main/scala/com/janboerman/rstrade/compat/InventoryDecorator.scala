package com.janboerman.rstrade.compat

import org.bukkit.inventory.{Inventory, ItemStack}

import scala.jdk.javaapi.CollectionConverters.asScala

object InventoryDecorator {
    implicit class TheDecorator(val inventory: Inventory) extends InventoryDecorator {
        //IndexedSeq
        implicit override def length(): Int = inventory.getSize
        implicit override def apply(index: Int): ItemStack = inventory.getItem(index)

        //Iterable
        implicit override def iterator(): Iterator[ItemStack] = asScala(inventory.iterator())

        implicit def isTradeInventory(): Boolean = inventory.isInstanceOf[TradeInventory]
    }
}

trait InventoryDecorator extends IndexedSeq[ItemStack] with Iterable[ItemStack] {
    /*
     * How to use:
     *
     * import com.janboerman.rstrade.compat.InventoryDecorator._
     * val inventory: org.bukkit.inventory.Inventory = ....
     * for (itemStack <- inventory) plugin.getLogger.info("ItemStack = " + itemStack)
     */
}
