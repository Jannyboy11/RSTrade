package com.janboerman.rstrade.compat.v1_12_R1.gui

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Buttons {

    val FillerStack: ItemStack = {
        val itemStack = new ItemStack(Material.STAINED_GLASS_PANE)
        itemStack.setDurability(9); //cyan
        val itemMeta = itemStack.getItemMeta
        itemMeta.setDisplayName(" ")
        itemStack.setItemMeta(itemMeta)
        itemStack
    }

}
