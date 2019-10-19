package com.janboerman.rstrade.compat.v1_12_R1.gui

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Buttons {

    val FillerStack: ItemStack = {
        //can't use guilib ItemBuilder#damage(int) because it casts the itemmeta to damageable!
        val itemStack = new ItemStack(Material.STAINED_GLASS_PANE)
        itemStack.setDurability(9); //cyan
        val itemMeta = itemStack.getItemMeta
        itemMeta.setDisplayName(" ")
        itemStack.setItemMeta(itemMeta)
        itemStack
    }

}
