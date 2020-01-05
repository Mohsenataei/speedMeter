package co.avalinejad.iq.util

import android.content.Context
import android.util.Log

class Preferences (context: Context){
    val context: Context = context.applicationContext

    val launchPreferences by lazy {
        context.getSharedPreferences(
            "LaunchPreferences",
            Context.MODE_PRIVATE
        )
    }

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

}