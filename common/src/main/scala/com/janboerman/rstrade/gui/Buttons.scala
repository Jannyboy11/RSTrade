package com.janboerman.rstrade.gui

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.janboerman.guilib.api.ItemBuilder

object Buttons {
    val AcceptStack: ItemStack = new ItemBuilder(Material.STRUCTURE_VOID).name("Accept").build()
    val CancelStack: ItemStack = new ItemBuilder(Material.BARRIER).name("Cancel").build()
    val ConfirmStack: ItemStack = new ItemBuilder(Material.STRUCTURE_VOID).name("Confirm").build()
    val DeclineStack: ItemStack = new ItemBuilder(Material.BARRIER).name("Decline").build()
    //TODO vault money button
}
