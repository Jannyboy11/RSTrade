package com.janboerman.rstrade.framework

import java.text.DecimalFormat

import org.bukkit.{ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.janboerman.guilib.api.ItemBuilder

import scala.collection.mutable

object Offer {
    implicit val itemOffer: Offer[ItemStack] = new Offer[ItemStack] {
        override def removeFromPlayer(offer: ItemStack, player: Player): Boolean = {
            val has = player.getInventory.containsAtLeast(offer, offer.getAmount)
            if (!has) false else {
                player.getInventory.remove(offer)
                true
            }
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

        override def removeFromPlayer(money: Money, player: Player): Boolean = {
            money.economy.withdrawPlayer(player, money.amount).transactionSuccess()
        }

        override def representation(money: Money): ItemStack = {
            new ItemBuilder(Material.GOLD_NUGGET)
                .name(money.economy.currencyNamePlural())
                .lore(ChatColor.RESET + "Amount: " + new DecimalFormat("#.##").format(money.amount))
                .build()
        }
    }
}

trait Offer[-T] {
    def giveToPlayer(offer: T, player: Player): Unit
    def removeFromPlayer(offer: T, player: Player): Boolean
    def representation(offer: T): ItemStack
}

case class Offers[T](offers: List[T])(implicit o: Offer[T]) {
    def getIcons(): List[ItemStack] = offers.map(offer => o.representation(offer))

    def withdrawFrom(player: Player): Boolean = {
        val removed: mutable.ListBuffer[T] = new mutable.ListBuffer[T]();
        var allRemoved = true

        val iterator = offers.iterator
        while (allRemoved && iterator.hasNext) {
            val offer = iterator.next()

            if (o.removeFromPlayer(offer, player)) {
                removed.addOne(offer)
            } else {
                allRemoved = false
                //give back the items
                for (rollback <- removed) {
                    o.giveToPlayer(rollback, player)
                }
            }
        }

        allRemoved
    }

    def giveTo(player: Player): Unit = {
        for (offer <- offers) {
            o.giveToPlayer(offer, player)
        }
    }
}