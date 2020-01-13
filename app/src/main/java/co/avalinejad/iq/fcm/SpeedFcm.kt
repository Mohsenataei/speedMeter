package co.avalinejad.iq.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pushpole.sdk.PushPole

class SpeedFcm : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (PushPole.getFcmHandler(this).onMessageReceived(remoteMessage)) {
            // Message is for PushPole
            return
        }
        super.onMessageReceived(remoteMessage)

        // Handle Firebase message
    }

    override fun onNewToken(s: String) {
        PushPole.getFcmHandler(this).onNewToken(s)
        super.onNewToken(s)

        // Token is refreshed
    }

    override fun onMessageSent(s: String) {
        PushPole.getFcmHandler(this).onMessageSent(s)
        super.onMessageSent(s)

        // Message sent
    }

    override fun onDeletedMessages() {
        PushPole.getFcmHandler(this).onDeletedMessages()
        super.onDeletedMessages()

        // Message was deleted
    }

    override fun onSendError(s: String, e: Exception) {
        PushPole.getFcmHandler(this).onSendError(s, e)
        super.onSendError(s, e)

        // Error sent
    }
}