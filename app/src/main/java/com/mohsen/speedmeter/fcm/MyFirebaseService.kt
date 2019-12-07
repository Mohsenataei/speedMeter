package com.mohsen.speedmeter.fcm

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("Registered")
class MyFirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        Log.d(TAG, "From: " + p0!!.from!!)
        if (p0.data.isNotEmpty()) {
            Log.d(TAG, "Push notification message data payload: " + p0.data)

            if(p0.data.containsKey("data")) {
                val jsonStr: String = p0.data["data"]!!

//                val intent = ReceptorActivity.createIntent(this, jsonStr)
//                startActivity(intent)
            }
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e("alz", "new firebase token: " + p0!!)
    }

    companion object {
        private val TAG = "MyFirebaseService"
    }

}