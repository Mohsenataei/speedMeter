package co.avalinejad.iq.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import co.avalinejad.iq.R
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
}
