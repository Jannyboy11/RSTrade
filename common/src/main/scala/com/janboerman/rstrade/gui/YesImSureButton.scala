package com.janboerman.rstrade.gui

import org.bukkit.event.inventory.InventoryClickEvent
import xyz.janboerman.guilib.api.menu.ItemButton

class YesImSureButton() extends ItemButton[AreYouSureGuiHolder](Buttons.ConfirmStack) {
    var isClickSuccess = false

    override def onClick(holder: AreYouSureGuiHolder, event: InventoryClickEvent): Unit = {
        if (!isClickSuccess) {

            if (holder.otherPlayerHasAccepted) {
                val myPlayer = holder.myPlayer
                val myOffers = holder.myOffers
                val otherPlayer = holder.otherPlayer
                val otherOffers = holder.otherOffers

                if (otherOffers.canGiveTo(myPlayer)) {
                    if (myOffers.canGiveTo(otherPlayer)) {
                        if (myOffers.canWidthDrawFrom(myPlayer)) {
                            if (otherOffers.canWidthDrawFrom(otherPlayer)) {
                                isClickSuccess = true //prevent multiple transfers
                                holder.setAccepted() //prevent duping through the InventoryClose listener

                                myOffers.withDrawFrom(myPlayer)
                                otherOffers.withDrawFrom(otherPlayer)

                                myOffers.giveTo(otherPlayer)
                                otherOffers.giveTo(myPlayer)

                                myPlayer.sendMessage("Trade successful!")
                                otherPlayer.sendMessage("Trade successful!")
                                close(holder)
                            } else {
                                myPlayer.sendMessage("The other player does not have enough resources")
                                otherPlayer.sendMessage("You don't have enough resources")
                                close(holder)
                            }
                        } else {
                            otherPlayer.sendMessage("The other player does not have enough resources")
                            myPlayer.sendMessage("You don't have enough resources")
                            close(holder)
                        }
                    } else {
                        myPlayer.sendMessage("The other player does not have enough room to accept your offers")
                        otherPlayer.sendMessage("You don't have enough room to accept the other player's offers")
                        close(holder)
                    }
                } else {
                    myPlayer.sendMessage("You don't have enough room to accept the other player's offers")
                    otherPlayer.sendMessage("The other player does not have enough room to accept your offers")
                    close(holder)
                }
            } else {
                holder.setAccepted() //also unsets the accept-button
                event.getWhoClicked.sendMessage("Waiting for other player..")
            }
        }
    }

    private def close(holder: AreYouSureGuiHolder) = holder.getPlugin.getServer.getScheduler.runTask(holder.getPlugin, new Runnable {
        override def run(): Unit = holder.close()
    })
}
