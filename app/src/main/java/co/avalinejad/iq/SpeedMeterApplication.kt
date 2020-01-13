package co.avalinejad.iq

import android.annotation.SuppressLint
import android.app.Application
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import co.avalinejad.iq.fragment.SelectLanguageDialogFragment
import com.batch.android.Batch
import com.batch.android.BatchActivityLifecycleHelper
import com.batch.android.Config


//import com.google.firebase.analytics.FirebaseAnalytics
import com.jaredrummler.android.device.DeviceName
import co.avalinejad.iq.network.RetrofitSingleton
import co.avalinejad.iq.util.Util.getDeviceName
import co.avalinejad.iq.util.Util.getIPAddress
import com.franmontiel.localechanger.LocaleChanger
import com.zeugmasolutions.localehelper.LocaleAwareApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

private val SHARED_PRE_KEY_ALREADY_SENT = "already_sent"
val APP_NAME = "sorat senj" //used for tracking installation

open class SpeedMeterApplication : LocaleAwareApplication() {

    lateinit var lanDialog : SelectLanguageDialogFragment

    companion object {
        //lateinit var appContext: Application
        lateinit var instance: SpeedMeterApplication
        private lateinit var appInstall: co.avalinejad.iq.model.AppInstall


        var isDataSentToServer = false
    }
     @SuppressLint("DefaultLocale")
     private fun setAppLocale(localeCode: String){
         Log.d("language" , "language changed to $localeCode")
         val dm = resources.displayMetrics
         val config = resources.configuration
         if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN){
             config.setLocale(Locale(localeCode.toLowerCase()))
         }else {
             config.locale = Locale(localeCode.toLowerCase())
         }
         resources.updateConfiguration(config, dm)
     }

    override fun onCreate() {
        super.onCreate()
        //appContext = this@SpeedMeterApplication
        instance = this
//        val lan = Preferences.getInstance(SpeedMeterApplication.instance.applicationContext).getLan()
//        Log.d("language", "default language is : $lan")
//
//        if (lan == ""){
//            var lang = ""
//            lanDialog = SelectLanguageDialogFragment(applicationContext, onResult = {
//                //                setAppLocale(it)
//                Log.d("language", "lancode received $it")
//                Toast.makeText(applicationContext,"lancode received $it", Toast.LENGTH_SHORT).show()
//                lang = it
////                lanDialog.dismiss()
//            })
//            setAppLocale(lang)
//        }
        val supportedLocales = listOf(
            Locale("en", "US"),
            Locale("fa", "IR")
        )

        LocaleChanger.initialize(applicationContext, supportedLocales)


        Batch.setConfig(Config(BuildConfig.channelName))
//        registerActivityLifecycleCallbacks(BatchActivityLifecycleHelper())

//        FirebaseAnalytics.getInstance(this)

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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleChanger.onConfigurationChanged()
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