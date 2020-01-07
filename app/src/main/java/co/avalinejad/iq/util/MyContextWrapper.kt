package co.avalinejad.iq.util

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import java.util.*
import android.annotation.TargetApi
import android.content.res.Configuration

class MyContextWrapper(val context: Context) : ContextWrapper(context){

    var mContext = context

    @SuppressWarnings("deprecation")
    fun wrap(lan:String) : ContextWrapper{
        val configuration = context.resources.configuration
        var systemLocale : Locale
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
            systemLocale = getSystemLocale(configuration)
        }else{
            systemLocale = getSystemLocaleLegacy(configuration)
        }

        if (lan != "" && systemLocale.language != lan ){
            val locale = Locale(lan)
            Locale.setDefault(locale)
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
                setSystemLocale(configuration,locale)
            }else {
                setSystemLocaleLegacy(configuration, locale)
            }
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1){
            mContext = context.createConfigurationContext(configuration)
        }else {
            mContext.resources.updateConfiguration(configuration,context.resources.displayMetrics)
        }
        return MyContextWrapper(mContext)
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun setSystemLocale(config: Configuration, locale: Locale) {
        config.setLocale(locale)
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun getSystemLocale(config: Configuration): Locale {
        return config.locales.get(0)
    }


    @SuppressWarnings("deprecation")
    fun getSystemLocaleLegacy(config: Configuration): Locale {
        return config.locale
    }

    @SuppressWarnings("deprecation")
    fun setSystemLocaleLegacy(config: Configuration, locale: Locale) {
        config.locale = locale
    }



}