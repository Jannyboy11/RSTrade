package xyz.janboerman.rstrade.compat

import org.bukkit.inventory.Inventory

//TODO move to an api package? probably should
trait TradeInventory extends Inventory {

    //TODO getPlayer() ?
    def getMyOffers : Inventory
    def getOtherOffers : Inventory
    def getButtons : Inventory

}
