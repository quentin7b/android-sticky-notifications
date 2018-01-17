package com.github.quentin7b.sn

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

object Tool {

    fun getLocale(context: Context): Locale {
        val configuration = context.resources.configuration
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getLocaleFromConfigurationNew(configuration)
        } else {
            getLocaleFromConfigurationOld(configuration)
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun getLocaleFromConfigurationNew(conf: Configuration): Locale {
        return conf.locales.get(0)
    }

    @Suppress("DEPRECATION")
    private fun getLocaleFromConfigurationOld(conf: Configuration): Locale {
        return conf.locale
    }


}
