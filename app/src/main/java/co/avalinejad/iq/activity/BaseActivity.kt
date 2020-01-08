package co.avalinejad.iq.activity

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.avalinejad.iq.fragment.SelectLanguageDialogFragment
import co.avalinejad.iq.util.NewContextWrapper
import android.os.Build
import android.annotation.TargetApi
import java.util.*




abstract class BaseActivity : AppCompatActivity() {

    var lan = ""
    //    val selectLanguageDialogFragment by lazy {
//        SelectLanguageDialogFragment(this,onResult = {
//            lan = it
//        })
//    }
     private lateinit var selectLanguageDialogFragment : SelectLanguageDialogFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lan = "fa"
        val selectLanguageDialogFragment = SelectLanguageDialogFragment(this, onResult = {
            Toast.makeText(this,"selected language is : $it",Toast.LENGTH_SHORT).show()
        })
        selectLanguageDialogFragment.show()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(this))
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
        selectLanguageDialogFragment = SelectLanguageDialogFragment(context,onResult ={
            lan = it
            Toast.makeText(context, "selected language is : $it",Toast.LENGTH_SHORT).show()
        })
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