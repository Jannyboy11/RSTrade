package com.janboerman.rstrade.gui

import Buttons.DeclineStack
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.janboerman.guilib.api.menu.ItemButton

object NoImUnsureButton extends ItemButton[AreYouSureGuiHolder](DeclineStack) {

    override def onClick(holder: AreYouSureGuiHolder, event: InventoryClickEvent): Unit = {
        holder.getPlugin.getServer.getScheduler.runTask(holder.getPlugin, new Runnable {
            override def run(): Unit = holder.close()
        })
    }

}
