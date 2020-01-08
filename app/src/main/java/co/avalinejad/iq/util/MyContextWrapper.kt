package co.avalinejad.iq.util

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import java.util.*

class MyContextWrapper(base: Context) : ContextWrapper(base) {
    companion object {
        @TargetApi(Build.VERSION_CODES.N)
        fun wrap(mContext: Context, newLocale: Locale): ContextWrapper {
            var context = mContext
            val res = context.resources
            val configuration = res.configuration
            when {
                Build.VERSION.SDK_INT > Build.VERSION_CODES.N -> {
                    configuration.setLocale(newLocale)
                    val localeList = LocaleList(newLocale)
                    LocaleList.setDefault(localeList)
//                    configuration.locales = localeList
                    configuration.setLocales(localeList)
                    context = context.createConfigurationContext(configuration)
                }
                Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2 -> {
                    configuration.setLocale(newLocale)
                    context = context.createConfigurationContext(configuration)
                }
                else -> {
                    @Suppress("DEPRECATION")
                    configuration.locale = newLocale
                    @Suppress("DEPRECATION")
                    res.updateConfiguration(configuration, res.displayMetrics)
                }
            }
            return ContextWrapper(context)
        }
    }
}