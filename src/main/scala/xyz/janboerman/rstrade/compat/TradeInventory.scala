package xyz.janboerman.rstrade.compat

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

//TODO move to an API package? probably should.
//TODO extend Scala traits such as IndexedSeq[ItemStack]? should probably use a package level implicit class for that
trait TradeInventory extends Inventory {

    def getMyOffers : Inventory
    def getOtherOffers : Inventory
    def getButtons : Inventory

    def getPlayer(): Player = getMyOffers.getHolder.asInstanceOf[Player]
    def getOtherPlayer(): Player = getOtherOffers.getHolder.asInstanceOf[Player]

}
