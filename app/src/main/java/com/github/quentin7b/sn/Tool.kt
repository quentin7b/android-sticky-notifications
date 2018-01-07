package com.github.quentin7b.sn

import android.content.Context
import android.os.Build
import java.util.*

/**
 * Created by quentin on 09/03/2017.
 */

object Tool {

    fun getLocale(context: Context): Locale {
        val configuration = context.resources.configuration
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.locales.get(0)
        } else {

            configuration.locale
        }
    }


}
