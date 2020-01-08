package co.avalinejad.iq.activity

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.avalinejad.iq.fragment.SelectLanguageDialogFragment
import co.avalinejad.iq.util.NewContextWrapper
import android.os.Build
import android.annotation.TargetApi
import co.avalinejad.iq.util.MyContextWrapper
import com.franmontiel.localechanger.LocaleChanger
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity
import java.util.*




abstract class BaseActivity : LocaleAwareCompatActivity() {

    var lan = ""
//     private lateinit var selectLanguageDialogFragment : SelectLanguageDialogFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun attachBaseContext(base: Context) {
        //val newBase = LocaleChanger.configureBaseContext(base)
        super.attachBaseContext(base)
    }

    private fun updateBaseContextLocale(context: Context):Context{
        promtUserToSelectLanguage(context)

        val locale = Locale(lan)
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
            return updateResourcesLocale(context,locale)
        }
        return updateResourcesLocaleLegacy(context,locale)
    }

    private fun promtUserToSelectLanguage(context: Context){
//        selectLanguageDialogFragment = SelectLanguageDialogFragment(context,onResult ={
//            lan = it
//            Toast.makeText(context, "selected language is : $it",Toast.LENGTH_SHORT).show()
//        })
    }


    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResourcesLocale(context: Context, locale: Locale): Context {
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }


    @SuppressWarnings("deprecation")
    private fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }


}