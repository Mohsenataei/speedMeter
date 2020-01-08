package co.avalinejad.iq.fragment

import android.app.Activity
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
import com.franmontiel.localechanger.LocaleChanger
import com.franmontiel.localechanger.utils.ActivityRecreationHelper


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
            onResult?.invoke("en")
            dismiss()
        }

        faBtn.setOnClickListener {
          onResult?.invoke("fa")
            dismiss()
        }
    }
    private fun setAppLocale(localeCode: String){
        Log.d("language" , "language changed to $localeCode")
        val res = SpeedMeterApplication.instance.resources
        SpeedMeterApplication.instance.baseContext
        val dm = res.displayMetrics
        val config = res.configuration
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN){
            config.setLocale(Locale(localeCode.toLowerCase()))
        }else {
            config.locale = Locale(localeCode.toLowerCase())
        }
        res.updateConfiguration(config,dm)
    }

    private fun changeAppLocale(lan: String){
        val locale = Locale(lan)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        SpeedMeterApplication.instance.baseContext.createConfigurationContext(config)
    }
}