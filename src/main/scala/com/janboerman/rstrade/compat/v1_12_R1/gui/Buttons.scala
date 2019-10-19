package com.janboerman.rstrade.compat.v1_12_R1.gui

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.janboerman.guilib.api.ItemBuilder

object Buttons {

    val FillerStack: ItemStack = new ItemBuilder(Material.STAINED_GLASS_PANE)
        .damage(9) //cyan
        .name(" ")
        .build();

}
