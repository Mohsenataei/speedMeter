package co.avalinejad.iq.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import java.util.*

class Preferences (context: Context){
    val context: Context = context.applicationContext

    val launchPreferences by lazy {
        context.getSharedPreferences(
            "LaunchPreferences",
            Context.MODE_PRIVATE
        )
    }
    val languagePrefrences by lazy {
        context.getSharedPreferences(
            "LanguagePreferences",
            Context.MODE_PRIVATE
        )
    }
    fun setLan(lan: String) = languagePrefrences.edit().putString(SELECTED_LANGUAGE,lan).apply()
    fun getLan() = languagePrefrences.getString(SELECTED_LANGUAGE, "")

    private fun setLaunchTimes(count: Int) = launchPreferences.edit().putInt("launchTimes", count).apply().let {
        Log.d("Preferences", "setLaunchTimes count is : $count")
    }

    fun incLaunchTimes() = setLaunchTimes(getLaunchTimes() + 1).let {
        Log.d("Preferences", "incLaunchTimes count is : $it")

    }

    fun getLaunchTimes() = launchPreferences.getInt("launchTimes", 0)

    fun resetLaunchTimes() = setLaunchTimes(0).also {
        Log.d("Preferences", "resetLaunchTimes invoked times is : ${getLaunchTimes()}")

    }


    fun setLaunchesBeforePrompt(launched: Int) = launchPreferences.edit().putInt("launchedBeforePrompt",launched).apply()

    fun setLaunchesBeforePrompt() = launchPreferences.getInt("launchedBeforePrompt",10)


    fun isSubmitRateOnGoogle()= launchPreferences.getBoolean("googleRate", false)

    fun submitRateOnGoogle() = launchPreferences.edit().putBoolean("googleRate",true).apply()





    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: Preferences? = null

        fun getInstance(context: Context): Preferences {
            if (instance == null)
                instance = Preferences(context)
            return instance!!
        }
    }
}