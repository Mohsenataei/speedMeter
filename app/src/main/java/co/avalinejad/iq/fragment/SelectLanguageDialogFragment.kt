package co.avalinejad.iq.fragment

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import co.avalinejad.iq.R
import kotlinx.android.synthetic.main.select_language_layout.*
import java.util.*
import android.os.Build
import co.avalinejad.iq.SpeedMeterApplication
import co.avalinejad.iq.util.Preferences


class SelectLanguageDialogFragment(
    context: Context,
    val onResult: ((String) -> Unit)? = null
    ) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_language_layout)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.attributes.windowAnimations = R.style.selectLanStyle
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        val resources = SpeedMeterApplication.instance.resources
        enBtn.setOnClickListener {
            Log.d("Language", "English selected.")
            setAppLocale("en")
//            val locale2 = Locale("fr")
//            Locale.setDefault(locale2)
//            val config2 = Configuration()
//            config2.locale = locale2
//            .getResources().updateConfiguration(
//                config2, getBaseContext().getResources().getDisplayMetrics()
//            )
//            // loading data ...
//            refresh()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                val configuration = resources.configuration
//                configuration.setLocale(Locale("en"))
//                SpeedMeterApplication.instance.applicationContext.createConfigurationContext(
//                    configuration
//                )
////                getApplicationContext().createConfigurationContext(configuration)
//                Preferences.getInstance(context).setLan("en")
//            } else {
//                val locale = Locale("en")
//                Locale.setDefault(locale)
//                val configuration = resources.configuration
////                val config = activity.getResources().getConfiguration()
//                configuration.locale = locale
//                resources.updateConfiguration(configuration, resources.displayMetrics)
//                Preferences.getInstance(context).setLan("en")
//            }
            onResult?.invoke("en")
            dismiss()
        }

        faBtn.setOnClickListener {
            Log.d("Language", "English selected.")
            setAppLocale("fa")
//            val locale2 = Locale("fr")
//            Locale.setDefault(locale2)
//            val config2 = Configuration()
//            config2.locale = locale2
//            .getResources().updateConfiguration(
//                config2, getBaseContext().getResources().getDisplayMetrics()
//            )
//            // loading data ...
//            refresh()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                val configuration = resources.configuration
//                configuration.setLocale(Locale("fa"))
//                SpeedMeterApplication.instance.applicationContext.createConfigurationContext(
//                    configuration
//                )
//                Preferences.getInstance(context).setLan("fa")
//
//
////                getApplicationContext().createConfigurationContext(configuration)
//            } else {
//                val locale = Locale("fa")
//                Locale.setDefault(locale)
//                val configuration = resources.configuration
////                val config = activity.getResources().getConfiguration()
//                configuration.locale = locale
//                resources.updateConfiguration(configuration, resources.displayMetrics)
//                Preferences.getInstance(context).setLan("fa")
//
//            }
            Log.d("Language", "farsi selected.")
            onResult?.invoke("fa")
            dismiss()
        }
    }
    private fun setAppLocale(localeCode: String){
        Log.d("language" , "language changed to $localeCode")
        val res = SpeedMeterApplication.instance.resources
        val dm = res.displayMetrics
        val config = res.configuration
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN){
            config.setLocale(Locale(localeCode.toLowerCase()))
        }else {
            config.locale = Locale(localeCode.toLowerCase())
        }
        res.updateConfiguration(config,dm)
    }
}