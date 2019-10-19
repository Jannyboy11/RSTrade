package com.janboerman.rstrade.framework

import net.milkbowl.vault.economy.Economy

case class Money(amount: Double)(implicit val economy: Economy) extends Product2[Double, Economy] {
    override def _1: Double = amount
    override def _2: Economy = economy
}