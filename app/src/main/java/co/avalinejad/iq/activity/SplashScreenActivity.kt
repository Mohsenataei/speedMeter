package co.avalinejad.iq.activity

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import co.avalinejad.iq.R
import co.avalinejad.iq.SpeedMeterApplication
import co.avalinejad.iq.fragment.SelectLanguageDialogFragment
import co.avalinejad.iq.util.NewContextWrapper
import co.avalinejad.iq.util.Preferences
import com.stepstone.apprating.AppRatingDialog
import com.stepstone.apprating.listener.RatingDialogListener
import kotlinx.android.synthetic.main.activity_splash_screen.*
import java.util.*

class SplashScreenActivity : BaseActivity(), RatingDialogListener {
    private lateinit var selectLanguageDialogFragment: SelectLanguageDialogFragment
    override fun onPositiveButtonClicked(rate: Int, comment: String) {
        Toast.makeText(
            this@SplashScreenActivity,
            "Rate : $rate\nComment : $comment",
            Toast.LENGTH_LONG
        ).show()
        Preferences.getInstance(this@SplashScreenActivity).resetLaunchTimes()
    }

    override fun onNegativeButtonClicked() {
        Toast.makeText(this@SplashScreenActivity, "Negative button clicked", Toast.LENGTH_LONG)
            .show()
    }

    override fun onNeutralButtonClicked() {
        Toast.makeText(this@SplashScreenActivity, "Neutral button clicked", Toast.LENGTH_LONG)
            .show()
    }

    val res = SpeedMeterApplication.instance.resources
    lateinit var lanDialog: SelectLanguageDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val animation: Animation
        val animation1: Animation
        val prefs = Preferences.getInstance(this)
        if (Preferences.getInstance(this).getLan() == "") {
            lanDialog = SelectLanguageDialogFragment(this, onResult = {
                Log.d("callback", "language received.")
                Preferences.getInstance(this).setLan(it)
                lan = it
                if (it == "en")
                    updateLocale(Locale.US)
                else
                    updateLocale(Locale("fa"))
            })
            lanDialog.show()
        }

        animation = AnimationUtils.loadAnimation(this, R.anim.slide_from_bottom)
        splash_screen_button.animation = animation
        splash_Screen_image_view.animation =
            AnimationUtils.loadAnimation(this, R.anim.slide_from_top)
        splash_screen_button.setOnClickListener {
            startActivity(Intent(this, NewSpeedMeter::class.java))
            finish()
        }
    }


    private fun initShowRateUsDialog() {
        AppRatingDialog.Builder()
            .setPositiveButtonText("Submit")
            .setNegativeButtonText("Cancel")
            .setNeutralButtonText("Later")
            .setNoteDescriptions(
                listOf(
                    res.getString(R.string.very_bad),
                    res.getString(R.string.not_good),
                    res.getString(R.string.quite_ok),
                    res.getString(R.string.very_good),
                    res.getString(R.string.excellent)
                )
            )
            .setDefaultRating(3)
            .setTitle(res.getString(R.string.rate_us))
            .setDescription(res.getString(R.string.plz_select_stars))
            .setStarColor(R.color.first_result)
            .setNoteDescriptionTextColor(R.color.color_tab)
            .setTitleTextColor(R.color.second_result)
            .setDescriptionTextColor(R.color.third_result)
            .setCommentTextColor(R.color.forth_result)
            .setCommentBackgroundColor(R.color.colorPrimaryDark)
            .setWindowAnimation(R.style.MyDialogSlideHorizontalAnimation)
            .setHint(res.getString(R.string.write_comment_here))
            .setHintTextColor(R.color.fifth_result)
            .setCancelable(false)
            .setCanceledOnTouchOutside(false)
            .create(this@SplashScreenActivity)
            .show()
    }

    override fun attachBaseContext(newBase: Context) {
//        Toast.makeText(newBase,"just for test.", Toast.LENGTH_SHORT).show()
        //val context = NewContextWrapper.wrap(newBase,"fa")
        super.attachBaseContext(newBase)
    }

    private fun updateBaseContextLocale(context: Context): Context {
        promptUserToSelectLanguage(context)

        val locale = Locale(lan)
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            return updateResourcesLocale(context, locale)
        }
        return updateResourcesLocaleLegacy(context, locale)
    }

    private fun promptUserToSelectLanguage(context: Context) {
        selectLanguageDialogFragment = SelectLanguageDialogFragment(context, onResult = {
            lan = it
            Toast.makeText(context, "selected language is : $it", Toast.LENGTH_SHORT).show()
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
