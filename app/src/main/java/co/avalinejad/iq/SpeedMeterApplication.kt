package co.avalinejad.iq

import android.app.Application
import com.batch.android.Batch
import com.batch.android.BatchActivityLifecycleHelper
import com.batch.android.Config


import com.google.firebase.analytics.FirebaseAnalytics
import com.jaredrummler.android.device.DeviceName
import co.avalinejad.iq.activity.LoginActivity.Companion.CHANNEL
import co.avalinejad.iq.network.RetrofitSingleton
import co.avalinejad.iq.util.Util.getDeviceName
import co.avalinejad.iq.util.Util.getIPAddress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

private val SHARED_PRE_KEY_ALREADY_SENT = "already_sent"
val APP_NAME = "sorat senj" //used for tracking installation

class SpeedMeterApplication : Application() {


    companion object {
        lateinit var appContext: Application

        private lateinit var appInstall: co.avalinejad.iq.model.AppInstall

        var isDataSentToServer = false
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this@SpeedMeterApplication

        Batch.setConfig(Config(co.avalinejad.iq.BuildConfig.channelName))
        registerActivityLifecycleCallbacks(BatchActivityLifecycleHelper())

        FirebaseAnalytics.getInstance(this)

        appInstall = co.avalinejad.iq.model.AppInstall()

        appInstall.userInfo.firstPageTime = Calendar.getInstance().timeInMillis.toString()

        DeviceName.with(this).request { info, error ->
            appInstall.mobile = co.avalinejad.iq.util.SecurePreferences.get(this, "phone")
            appInstall.type = "open"
            appInstall.userInfo.manufacturer = info.manufacturer
            appInstall.userInfo.name = info.marketName
            appInstall.userInfo.model = info.model
            appInstall.userInfo.codename = info.codename
            appInstall.userInfo.deviceName = info.name
            appInstall.userInfo.phoneNumber = co.avalinejad.iq.util.SecurePreferences.get(this, "phone")
            sendServerUserData()
        }

       // if (isMCI().not())
         //   CharkhoneSdkApp.initSdk(this, getSecrets())

    }


    private fun sendServerUserData() {
        appInstall.userInfo.ip = getIPAddress(true)
        if (appInstall.userInfo.deviceName.isNullOrEmpty()) {
            val deviceCode = getDeviceName()
            appInstall.userInfo.codename = deviceCode
            appInstall.userInfo.deviceName = DeviceName.getDeviceName(
                deviceCode,
                "Unknown device"
            )
        }

        sendAppInstall()
    }

    private fun sendAppInstall() {
        appInstall.appName = APP_NAME
        appInstall.channel = CHANNEL
        appInstall.androidId = co.avalinejad.iq.util.InstallationHelper.getAndroidId(this)
        appInstall.installationId = co.avalinejad.iq.util.InstallationHelper.getInstallationId(this)
        appInstall.initUserInfo()

        RetrofitSingleton.getWebService().addAppInstall(appInstall).enqueue(object :
            Callback<co.avalinejad.iq.model.AppInstallResponse> {
            override fun onResponse(call: Call<co.avalinejad.iq.model.AppInstallResponse>, response: Response<co.avalinejad.iq.model.AppInstallResponse>) {
                co.avalinejad.iq.util.SecurePreferences.put(this@SpeedMeterApplication, SHARED_PRE_KEY_ALREADY_SENT, "true")
            }

            override fun onFailure(call: Call<co.avalinejad.iq.model.AppInstallResponse>, t: Throwable) {
                t.printStackTrace()
                //Do nothing
            }
        })
    }
}