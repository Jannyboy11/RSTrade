package com.janboerman.rstrade.gui

import com.janboerman.rstrade._
import com.janboerman.rstrade.extensions.InventorySeq._
import com.janboerman.rstrade.extensions.TradePlayer._
import com.janboerman.rstrade.framework.{Offers, TradeInventory}
import org.bukkit.event.inventory.{InventoryClickEvent, InventoryDragEvent, InventoryOpenEvent}
import xyz.janboerman.guilib.api.GuiInventoryHolder

class TradeGuiHolder(tradeInventory: TradeInventory)(implicit plugin: TradePlugin) extends GuiInventoryHolder(plugin, tradeInventory) {

    override def getInventory(): TradeInventory = super.getInventory().asInstanceOf[TradeInventory]

    private var firstTimeOpen = true

    override def onOpen(event: InventoryOpenEvent): Unit = {
        //set buttons
        if (firstTimeOpen) {
            val inventory = getInventory()
            inventory.setItem(4, Buttons.AcceptStack)
            inventory.setItem(49, Buttons.CancelStack)
            for (i <- 13 to(40, 9)) {
                inventory.setItem(i, plugin.getServerVersion().newFillerStack())
            }
            firstTimeOpen = false
        }
    }

    override def onDrag(event: InventoryDragEvent): Unit = {
        event.setCancelled(false)
    }

    override def onClick(event: InventoryClickEvent): Unit = {
        event.setCancelled(false)

        val inventory = getInventory()
        if (GuiInventoryHolder.getClickedInventory(event) == inventory && inventory.getPlayer() == event.getWhoClicked) {
            val myPlayer = inventory.getPlayer()
            val otherPlayer = inventory.getOtherPlayer()

            event.getSlot match {
                case x if x % 9 < 4 =>
                    inventory.isAccepted = false

                case 4 /* accept */ =>
                    inventory.isAccepted = true

                    if (inventory.isOtherAccepted) {
                        val myOffers = Offers(inventory.getMyOffers.filter(_ != null).toList)
                        val otherOffers = Offers(inventory.getOtherOffers.filter(_ != null).toList)

                        plugin.getServer.getScheduler.runTask(plugin, new Runnable {
                            override def run(): Unit = {
                                myPlayer.closeInventory()
                                if (otherPlayer.isTrading()) otherPlayer.closeInventory()

                                plugin.getServer.getScheduler.runTask(plugin, new Runnable {
                                    override def run(): Unit = {
                                        val (areYouSureOne, areYouSureTwo) = AreYouSureGuiHolder(myPlayer, otherPlayer, myOffers, otherOffers)
                                        myPlayer.openInventory(areYouSureOne.getInventory)
                                        otherPlayer.openInventory(areYouSureTwo.getInventory)
                                    }
                                })
                            }
                        })
                    } else {
                        event.getWhoClicked.sendMessage("Waiting for other player to accept..")
                    }

                case 49 /* cancel */ =>
                    inventory.isAccepted = false

                    plugin.getServer.getScheduler.runTask(plugin, new Runnable {
                        override def run(): Unit = {
                            inventory.close()
                        }
                    })

                case _ => inventory.isAccepted = false
            }
        }
    }
}
