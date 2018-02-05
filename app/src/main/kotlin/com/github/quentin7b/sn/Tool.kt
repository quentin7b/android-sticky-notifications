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
            getLocaleNewAPI(configuration)
        } else {
            getLocaleOldAPI(configuration)
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun getLocaleNewAPI(configuration: Configuration): Locale {
        return configuration.locales.get(0)
    }

    @Suppress("DEPRECATION")
    private fun getLocaleOldAPI(configuration: Configuration): Locale {
        return configuration.locale
    }
}
