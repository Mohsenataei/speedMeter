package co.avalinejad.iq.activity

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.avalinejad.iq.SpeedMeterApplication
import co.avalinejad.iq.fragment.SelectLanguageDialogFragment
import co.avalinejad.iq.util.MyContextWrapper
import co.avalinejad.iq.util.NewContextWrapper

abstract class BaseActivity() : AppCompatActivity() {

    var lan = ""
    //    val selectLanguageDialogFragment by lazy {
//        SelectLanguageDialogFragment(this,onResult = {
//            lan = it
//        })
//    }
    // private lateinit var selectLanguageDialogFragment : SelectLanguageDialogFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lan = "fa"
        val selectLanguageDialogFragment = SelectLanguageDialogFragment(this, onResult = {
            Toast.makeText(this,"selected language is : $it",Toast.LENGTH_SHORT).show()
        })
        selectLanguageDialogFragment.show()
    }

    override fun attachBaseContext(base: Context?) {
        var language = ""
       val selectLanguageDialogFragment = SelectLanguageDialogFragment(this,onResult = {
            language = it
        })
        selectLanguageDialogFragment.show()

        super.attachBaseContext(NewContextWrapper.wrap(base, "fa"))
    }


}