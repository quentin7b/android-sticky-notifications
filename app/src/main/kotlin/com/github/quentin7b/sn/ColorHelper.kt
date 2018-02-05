package com.github.quentin7b.sn

import android.support.annotation.ColorRes

import com.github.quentin7b.sn.database.model.StickyNotification

import java.util.HashMap

object ColorHelper {

    private val DEFCON_COLORS = BiMap()

    init {
        DEFCON_COLORS[StickyNotification.Defcon.USELESS] = R.color.color_useless
        DEFCON_COLORS[StickyNotification.Defcon.NORMAL] = R.color.color_normal
        DEFCON_COLORS[StickyNotification.Defcon.IMPORTANT] = R.color.color_important
        DEFCON_COLORS[StickyNotification.Defcon.ULTRA] = R.color.color_ultra
    }

    @ColorRes
    fun getDefconColor(defcon: StickyNotification.Defcon): Int {
        return DEFCON_COLORS.item(defcon)
    }

    fun getColorDefcon(@ColorRes colorRes: Int?): StickyNotification.Defcon {
        return DEFCON_COLORS.from(colorRes)
    }

    private class BiMap : HashMap<StickyNotification.Defcon, Int>() {

        fun from(integer: Int?): StickyNotification.Defcon {
            for ((key, value) in entries) {
                if (value == integer) {
                    return key
                }
            }
            throw IllegalArgumentException("No value for this integer")
        }

        fun item(defcon: StickyNotification.Defcon?): Int {
            for ((key, value) in entries) {
                if (key == defcon) {
                    return value
                }
            }
            throw IllegalArgumentException("No value for this defcon")
        }

    }
}
