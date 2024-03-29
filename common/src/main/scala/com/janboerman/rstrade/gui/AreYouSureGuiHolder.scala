package com.janboerman.rstrade.gui

import com.janboerman.rstrade.TradePlugin
import com.janboerman.rstrade.framework.Offers
import org.bukkit.entity.Player
import org.bukkit.event.inventory.{InventoryCloseEvent, InventoryOpenEvent}
import org.bukkit.inventory.InventoryView
import xyz.janboerman.guilib.api.menu.{ItemButton, MenuHolder}

object AreYouSureGuiHolder {
    def apply(playerOne: Player, playerTwo: Player, offersOne: Offers[_], offersTwo: Offers[_])(implicit plugin: TradePlugin): (AreYouSureGuiHolder, AreYouSureGuiHolder) = {
        val one: AreYouSureGuiHolder = new AreYouSureGuiHolder(playerOne, playerTwo, offersOne, offersTwo)
        val two: AreYouSureGuiHolder = new AreYouSureGuiHolder(playerTwo, playerOne, offersTwo, offersOne)

        one.other = two
        two.other = one

        (one, two)
    }
}

class AreYouSureGuiHolder private(val myPlayer: Player,
                                  val otherPlayer: Player,
                                  val myOffers: Offers[_],
                                  val otherOffers: Offers[_])
                                 (implicit plugin: TradePlugin)
    extends MenuHolder[TradePlugin](plugin, {
        val items = Math.max(myOffers.getIcons().size, otherOffers.getIcons().size);
        var rows = items / 4
        if (items % 4 != 0) rows += 1
        Math.max(2, rows) * 9
    }, "Are you sure?") {


    def close(): Unit = {
        myPlayer.getOpenInventory match {
            case view: InventoryView if view.getTopInventory.getHolder == this =>
                view.close()
            case _ => ()
        }
        closeOther()
    }
    private def closeOther(): Unit = {
        otherPlayer.getOpenInventory match {
            case view: InventoryView if view.getTopInventory.getHolder.isInstanceOf[AreYouSureGuiHolder] =>
                val otherConfirmScreen = view.getTopInventory.getHolder.asInstanceOf[AreYouSureGuiHolder]
                if (otherConfirmScreen.otherPlayer.eq(myPlayer)
                    && otherConfirmScreen.otherOffers.eq(myOffers)
                    && otherConfirmScreen.myOffers.eq(otherOffers)) {
                    view.close()
                }
            case _ => ()
        }
    }


    private val confirmIndex = 4
    private val cancelIndex = getInventory.getSize - 5

    private var hasAccepted = false
    private var other: AreYouSureGuiHolder = _

    def otherPlayerHasAccepted: Boolean = other.hasAccepted

    def setAccepted(): Unit = {
        hasAccepted = true
        unsetButton(confirmIndex)
    }

    def isAccepted(): Boolean = hasAccepted && otherPlayerHasAccepted

    override def onOpen(event: InventoryOpenEvent): Unit = {
        val myOffersIndices = (0 to 5).flatMap(y => (0 to 3).map(x => 9 * y + x))
        val otherOffersIncides = myOffersIndices.map(_ + 5)

        myOffers.getIcons().zip(myOffersIndices).foreach { case (icon, idx) => setButton(idx, new ItemButton(icon)) }
        otherOffers.getIcons().zip(otherOffersIncides).foreach { case (icon, idx) => setButton(idx, new ItemButton(icon)) }

        setButton(confirmIndex, new YesImSureButton())
        setButton(cancelIndex, NoImUnsureButton)
    }

    override def onClose(event: InventoryCloseEvent): Unit = {
        clearButtons()
        getPlugin.getServer.getScheduler.runTask(getPlugin, new Runnable {
            override def run(): Unit = closeOther()
        })
    }

}
