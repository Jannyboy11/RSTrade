package xyz.janboerman.rstrade.compat

import org.bukkit.inventory.{Inventory, ItemStack}

import scala.collection.JavaConverters

object InventoryDecorator {
    implicit class TheDecorator(val inventory: Inventory) extends InventoryDecorator {
        //IndexedSeq
        implicit override def length(): Int = inventory.getSize
        implicit override def apply(index: Int): ItemStack = inventory.getItem(index)

        //Traversable
        implicit override def foreach[U](f: ItemStack => U): Unit = inventory.getContents.foreach(f)

        //Iterable
        implicit override def iterator(): Iterator[ItemStack] = JavaConverters.asScalaIterator(inventory.iterator())
    }
}

trait InventoryDecorator extends IndexedSeq[ItemStack] with Traversable[ItemStack] with Iterable[ItemStack] {
    /*
     * How to use:
     *
     * import xyz.janboerman.rstrade.compat.InventoryDecorator._
     * val inventory: org.bukkit.inventory.Inventory = ....
     * for (itemStack <- inventory) plugin.getLogger.info("ItemStack = " + itemStack)
     */
}
