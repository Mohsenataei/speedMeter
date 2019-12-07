package com.mohsen.speedmeter.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.mohsen.speedmeter.BuildConfig

import com.mohsen.speedmeter.R
import com.mohsen.speedmeter.SpeedMeterApplication
import com.mohsen.speedmeter.extention.batchRequestPermissions
import com.mohsen.speedmeter.extention.shouldShowPermissionRationale
import com.mohsen.speedmeter.network.RetrofitSingleton
import com.mohsen.speedmeter.util.USER
import com.mohsen.speedmeter.util.isIrancell
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap
import okhttp3.MultipartBody

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isUserLoggedIn =  com.mohsen.speedmeter.util.SecurePreferences.get(this, USER) != null
        if(isIrancell() && !isUserLoggedIn) {
            com.mohsen.speedmeter.util.Util.openActivity(this, IntroActivity::class.java, HashMap<String, String>(), 0)
            finish()

        }

        checkForUpdate()

        setContentView(R.layout.activity_main)

        requestContactsPermissions()



    }
    companion object {

        const val TAG = "MainActivity"
        const val REQUEST_LOCATION = 1
        val PERMISSIONS_LOCATION = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun requestContactsPermissions() {
        if (shouldShowPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
            shouldShowPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            batchRequestPermissions(PERMISSIONS_LOCATION, REQUEST_LOCATION)
        }else
            batchRequestPermissions(PERMISSIONS_LOCATION, REQUEST_LOCATION)
    }


    override fun onBackPressed() {
        if (container.visibility == View.VISIBLE) {
//            supportFragmentManager.popBackStack()
            container.visibility = View.GONE
        }
        else
            com.mohsen.speedmeter.util.ExitHelper.exitOnSecondBackPress(this)
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
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {

        if (requestCode == REQUEST_LOCATION) {
            Log.i(TAG, "Received response for contact permissions request.")

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                // All required permissions have been granted, display contacts fragment.
                if(!SpeedMeterApplication.isDataSentToServer)
                    sendFCMTokenToServer()

            } else {
                Log.i(TAG, "Contacts permissions were NOT granted.")
                Toast.makeText(this,getString(R.string.access_permission_notif), Toast.LENGTH_SHORT).show()
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @SuppressLint("MissingPermission") //since we call this function after permission grant
    fun sendFCMTokenToServer(){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener<InstanceIdResult> {
                if (!it.isSuccessful) {
                    return@OnCompleteListener
                }

                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: return@OnCompleteListener

                val longitude = location.longitude
                val latitude = location.latitude
                val token = it.result!!.token

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("token", token)
                    .addFormDataPart("lat", latitude.toString())
                    .addFormDataPart("lng", longitude.toString())
                    .build()

                RetrofitSingleton.getWebService().sendUserLocation(requestBody)
                    .enqueue(object: Callback<com.mohsen.speedmeter.network.EmptyResponse> {
                        override fun onFailure(call: Call<com.mohsen.speedmeter.network.EmptyResponse>, t: Throwable) {
                            SpeedMeterApplication.isDataSentToServer = true
                        }

                        override fun onResponse(call: Call<com.mohsen.speedmeter.network.EmptyResponse>, response: Response<com.mohsen.speedmeter.network.EmptyResponse>) {
                            //do nothing
                        }
                    })
            })
    }

}
