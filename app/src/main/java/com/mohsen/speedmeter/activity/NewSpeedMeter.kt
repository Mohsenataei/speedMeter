package com.mohsen.speedmeter.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.mohsen.speedmeter.BuildConfig


import com.mohsen.speedmeter.network.RetrofitSingleton
import com.mohsen.speedmeter.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewSpeedMeter : AppCompatActivity() , ActivityCompat.OnRequestPermissionsResultCallback{
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForUpdate()
        setContentView(R.layout.activity_new_speed_meter)

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
           BuildConfig.VERSION_CODE)
            .enqueue(object : Callback<com.mohsen.speedmeter.component.update.Repo.model.UpdateResult> {
                override fun onResponse(call: Call<com.mohsen.speedmeter.component.update.Repo.model.UpdateResult>, response: Response<com.mohsen.speedmeter.component.update.Repo.model.UpdateResult>) {
                    val code = response.code()
                    val body = response.body()
                    if (body != null && code > 199 && code < 300)
                        com.mohsen.speedmeter.component.update.UpdateFragment.newInstance(body).show(supportFragmentManager, "new app available")

                }

                override fun onFailure(call: Call<com.mohsen.speedmeter.component.update.Repo.model.UpdateResult>, t: Throwable) {}
            })

    }
}
