package com.janboerman.rstrade.gui

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import xyz.janboerman.guilib.api.ItemBuilder
import xyz.janboerman.guilib.api.menu.ItemButton


//TODO can be an object singleton?
object YesImSureButton extends ItemButton[AreYouSureGuiHolder](Buttons.ConfirmStack) {
    var isClickSuccess = false

    override def onClick(holder: AreYouSureGuiHolder, event: InventoryClickEvent): Unit = {
        if (holder.otherPlayerHasAccepted) {
            val myPlayer = holder.myPlayer
            val myOffers = holder.myOffers
            val otherPlayer = holder.otherPlayer
            val otherOffers = holder.otherOffers

            if (otherOffers.withdrawFrom(otherPlayer)) {
                if (myOffers.withdrawFrom(myPlayer)) {

                    if (!isClickSuccess) {
                        close(holder)

                        //TODO this does not seem to work. rework rewards work.
                        holder.myOffers.giveTo(holder.otherPlayer)
                        holder.otherOffers.giveTo(holder.myPlayer)
                        isClickSuccess = true //prevent duplication
                    }
                } else {
                    //my player could not withdraw
                    //TODO send a message 'you don't have enough resources'

                    close(holder)
                }
            } else {
                //other player could not withdraw
                //TODO send a message 'the other player did not have enough resources'

                close(holder)
            }

        } else {
            holder.setAccepted() //also unsets the accept-button
            event.getWhoClicked.sendMessage("Waiting for other player..")
        }
    }

    private def close(holder: AreYouSureGuiHolder) = holder.getPlugin.getServer.getScheduler.runTask(holder.getPlugin, new Runnable {
        override def run(): Unit = holder.close()
    })
}
