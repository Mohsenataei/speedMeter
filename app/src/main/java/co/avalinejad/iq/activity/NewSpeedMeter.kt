package co.avalinejad.iq.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.gms.ads.MobileAds
import co.avalinejad.iq.network.RetrofitSingleton
import co.avalinejad.iq.R
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_new_speed_meter.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class NewSpeedMeter : BaseActivity() , ActivityCompat.OnRequestPermissionsResultCallback{
    lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        checkForUpdate()

        setContentView(R.layout.activity_new_speed_meter)
        MobileAds.initialize(this,"ca-app-pub-8616739363688136~4130606453")

        val adRequest: AdRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()
//        val adRequest = AdRequest.Builder()
//            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//            .build()

        // Start loading the ad in the background.
        ad_view.loadAd(adRequest)

        ad_view1.loadAd(adRequest)


//        val fragmentManager  = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//
//        val speedMeterIntroFragment = SpeedMeterIntroFragment
//        fragmentTransaction.add(R.id.containerFragment,speedMeterIntroFragment.newInstance(),"")
//        fragmentTransaction.commit()

        //requestContactsPermissions()
        navController = findNavController(R.id.containerFragment)

    }



    private fun checkForUpdate() {
        RetrofitSingleton.getWebService().getResults("Android", "roja",
           co.avalinejad.iq.BuildConfig.VERSION_CODE)
            .enqueue(object : Callback<co.avalinejad.iq.component.update.Repo.model.UpdateResult> {
                override fun onResponse(call: Call<co.avalinejad.iq.component.update.Repo.model.UpdateResult>, response: Response<co.avalinejad.iq.component.update.Repo.model.UpdateResult>) {
                    val code = response.code()
                    val body = response.body()
                    if (body != null && code > 199 && code < 300)
                        co.avalinejad.iq.component.update.UpdateFragment.newInstance(body).show(supportFragmentManager, "new app available")

                }

                override fun onFailure(call: Call<co.avalinejad.iq.component.update.Repo.model.UpdateResult>, t: Throwable) {}
            })

    }
    public override fun onPause() {
        ad_view.pause()
        super.onPause()
    }

    // Called when returning to the activity
    public override fun onResume() {
        super.onResume()
        ad_view.resume()
    }

    // Called before the activity is destroyed
    public override fun onDestroy() {
        ad_view.destroy()
        super.onDestroy()
    }
}
