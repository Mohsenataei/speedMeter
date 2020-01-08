package co.avalinejad.iq.activity

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import co.avalinejad.iq.R
import co.avalinejad.iq.SpeedMeterApplication
import co.avalinejad.iq.fragment.SelectLanguageDialogFragment
import co.avalinejad.iq.util.LAUNCHES_BEFORE_RATE_US_DIALOG
import co.avalinejad.iq.util.Preferences
import com.stepstone.apprating.AppRatingDialog
import com.stepstone.apprating.listener.RatingDialogListener
import kotlinx.android.synthetic.main.activity_splash_screen.*
import java.util.*
import android.content.ActivityNotFoundException

class SplashScreenActivity : BaseActivity(), RatingDialogListener {
    private lateinit var selectLanguageDialogFragment: SelectLanguageDialogFragment
    override fun onPositiveButtonClicked(rate: Int, comment: String) {
//        Toast.makeText(
//            this@SplashScreenActivity,
//            "Rate : $rate\nComment : $comment",
//            Toast.LENGTH_LONG
//        ).show()
        if (rate >= 3) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
//            goToGooglePlay()
            Preferences.getInstance(this@SplashScreenActivity).submitRateOnGoogle()
        }
    }

    override fun onNegativeButtonClicked() {
//        Toast.makeText(this@SplashScreenActivity, "Negative button clicked", Toast.LENGTH_LONG)
//            .show()
    }

    override fun onNeutralButtonClicked() {
//        Toast.makeText(this@SplashScreenActivity, "Neutral button clicked", Toast.LENGTH_LONG) // by clicking on later
//            .show()
//        Preferences.getInstance(this@SplashScreenActivity).setLaunchesBeforePrompt(5)
    }

    val res = SpeedMeterApplication.instance.resources
    lateinit var lanDialog: SelectLanguageDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val animation: Animation
        val animation1: Animation

        val prefs = Preferences.getInstance(this)
        LAUNCHES_BEFORE_RATE_US_DIALOG = prefs.setLaunchesBeforePrompt()

        Log.d("rateUs", "this application has bees launched ${prefs.getLaunchTimes()} till now.")
        if (prefs.getLaunchTimes() > 8 && !prefs.isSubmitRateOnGoogle()) {
            initShowRateUsDialog()
            prefs.resetLaunchTimes()
            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)))
            //prefs.resetLaunchTimes()
            Log.d(
                "rateUs",
                "launched times reset to 0 and prefs.launchtimes is : ${prefs.getLaunchTimes()}"
            )
        } else {
            Log.d("rateUs", "times before ${prefs.getLaunchTimes()}")
            prefs.incLaunchTimes()
            Log.d(
                "rateUs", "launch times increased. times after : ${prefs.getLaunchTimes()}"
            )

        }

        selectLang.setOnClickListener {
            selectLanguageDialogFragment = SelectLanguageDialogFragment(this, onResult = {
                updateLocale(Locale(it))
            })
            selectLanguageDialogFragment.show()
        }

        animation = AnimationUtils.loadAnimation(this, R.anim.slide_from_bottom)
        splash_screen_button.animation = animation
        selectLang.animation = AnimationUtils.loadAnimation(this, R.anim.slide_left)
//        selectLang.animation = AnimationUtils.loadAnimation(this,R.anim.slide_right)

        splash_Screen_image_view.animation =
            AnimationUtils.loadAnimation(this, R.anim.slide_from_top)
        splash_screen_button.setOnClickListener {
            startActivity(Intent(this, NewSpeedMeter::class.java))
            finish()
        }
    }


    private fun initShowRateUsDialog() {
        AppRatingDialog.Builder()
            .setPositiveButtonText(res.getString(R.string.submit))
            .setNegativeButtonText(res.getString(R.string.cancel))
            .setNeutralButtonText(res.getString(R.string.later))
            .setNoteDescriptions(
                listOf(
                    res.getString(R.string.very_bad),
                    res.getString(R.string.not_good),
                    res.getString(R.string.quite_ok),
                    res.getString(R.string.very_good),
                    res.getString(R.string.excellent)
                )
            )
            .setDefaultRating(5)
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
            //Toast.makeText(context, "selected language is : $it", Toast.LENGTH_SHORT).show()
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

    private fun goToGooglePlay() {
        val uri = Uri.parse("market://details?id=$packageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }

    }


}
