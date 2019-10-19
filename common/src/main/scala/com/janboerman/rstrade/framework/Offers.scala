package com.janboerman.rstrade.framework

import java.text.DecimalFormat

import org.bukkit.{ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.janboerman.guilib.api.ItemBuilder

object Offer {
    implicit val itemOffer: Offer[ItemStack] = new Offer[ItemStack] {

        override def canGiveToPlayer(offer: ItemStack, player: Player): Boolean = {
            val offerClone = offer.clone()

            def canFit(into: ItemStack): Int = {
                if (into == null) offerClone.getAmount
                else if (into.isSimilar(offerClone)) into.getMaxStackSize - into.getAmount
                else 0
            }

            var remaining = offerClone.getAmount
            for (is <- player.getInventory.getStorageContents if remaining > 0) {
                remaining -= canFit(is)
                if (remaining > 0)
                    offerClone.setAmount(remaining)
                else return true
            }

            false
        }

        override def canRemoveFromPlayer(offer: ItemStack, player: Player): Boolean = {
            player.getInventory.containsAtLeast(offer, offer.getAmount)
        }

        override def removeFromPlayer(offer: ItemStack, player: Player): Unit = {
            player.getInventory.removeItem(offer)
        }

        override def giveToPlayer(offer: ItemStack, player: Player): Unit = {
            val map = player.getInventory.addItem(offer)
            if (!map.isEmpty) {
                player.getWorld.dropItem(player.getLocation, map.get(Integer.valueOf(0)))
            }
        }

        override def representation(offer: ItemStack): ItemStack = offer.clone()

    }

    implicit val moneyOffer: Offer[Money] = new Offer[Money] {
        override def giveToPlayer(money: Money, player: Player): Unit = {

            money.economy.depositPlayer(player, money.amount)
        }

        override def canRemoveFromPlayer(money: Money, player: Player): Boolean = {
            money.economy.has(player, money.amount)
        }

        override def removeFromPlayer(money: Money, player: Player): Unit = {
            money.economy.withdrawPlayer(player, money.amount)
        }

        override def representation(money: Money): ItemStack = {
            new ItemBuilder(Material.GOLD_NUGGET)
                .name(money.economy.currencyNamePlural())
                .lore(s"${ChatColor.RESET}Amount: ${new DecimalFormat("#.##").format(money.amount)}")
                .build()
        }

        override def canGiveToPlayer(money: Money, player: Player): Boolean = {
            money.economy.getBalance(player) + money.amount < Double.MaxValue
        }
    }
}

trait Offer[-T] {
    def canGiveToPlayer(offer: T, player: Player): Boolean
    def giveToPlayer(offer: T, player: Player): Unit
    def canRemoveFromPlayer(offer: T, player: Player): Boolean
    def removeFromPlayer(offer: T, player: Player): Unit
    def representation(offer: T): ItemStack
}

case class Offers[T](offers: List[T])(implicit o: Offer[T]) {
    def getIcons(): List[ItemStack] = offers.map(o.representation(_))

    def withDrawFrom(player: Player): Unit = offers.foreach(o.removeFromPlayer(_, player))

    def canWidthDrawFrom(player: Player): Boolean = offers.forall(o.canRemoveFromPlayer(_, player))

    def canGiveTo(player: Player): Boolean = offers.forall(o.canGiveToPlayer(_, player))

    def giveTo(player: Player): Unit = offers.foreach(o.giveToPlayer(_, player))
}