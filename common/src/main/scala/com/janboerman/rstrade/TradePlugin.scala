package com.janboerman.rstrade

import com.janboerman.rstrade.compat.ServerVersion
import org.bukkit.plugin.Plugin

trait TradePlugin extends Plugin {

    def getServerVersion(): ServerVersion

}
