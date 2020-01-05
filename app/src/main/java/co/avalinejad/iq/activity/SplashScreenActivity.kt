package co.avalinejad.iq.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import co.avalinejad.iq.R
import com.stepstone.apprating.AppRatingDialog
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {

   // val fromButtom? : Animation = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val animation: Animation
        val animation1: Animation

        animation = AnimationUtils.loadAnimation(this, R.anim.slide_from_bottom)
        splash_screen_button.animation = animation
        splash_Screen_image_view.animation = AnimationUtils.loadAnimation(this,R.anim.slide_from_top)
        splash_screen_button.setOnClickListener{
            startActivity(Intent(this,NewSpeedMeter::class.java))
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
            .setDefaultRating(2)
            .setTitle(res.getString(R.string.rate_us))
            .setDescription(res.getString(R.string.plz_select_stars))
            .setStarColor(R.color.gray)
            .setNoteDescriptionTextColor(R.color.color_tab)
            .setTitleTextColor(R.color.bmi_more_than_40)
            .setDescriptionTextColor(R.color.bmi_below_18_5)
            .setCommentTextColor(R.color.bmi_18_5_to_20)
            .setCommentBackgroundColor(R.color.colorPrimaryDark)
            .setWindowAnimation(R.style.MyDialogSlideHorizontalAnimation)
            .setHint(res.getString(R.string.write_comment_here))
            .setHintTextColor(R.color.hintTextColor)
            .setCancelable(false)
            .setCanceledOnTouchOutside(false)
            .create(this@SplashScreenActivity)
            .show()
    }

}
