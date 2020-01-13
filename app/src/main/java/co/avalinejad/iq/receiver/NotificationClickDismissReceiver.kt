package co.avalinejad.iq.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.avalinejad.iq.eventbus.MessageEvent
import org.greenrobot.eventbus.EventBus
import java.util.HashMap

class NotificationClickDismissReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action

        val data = HashMap<String, String>()
        if (action == null) return
        if (action == context!!.packageName + ".pusheco.NOTIF_CLICKED") {
            data["event"] = "notification_clicked"
            EventBus.getDefault().post(MessageEvent(data.toString()))
        } else if (action == context.getPackageName() + ".pusheco.NOTIF_DISMISSED") {
            data["event"] = "notification_dismissed"
            EventBus.getDefault().post(MessageEvent(data.toString()))
        } else if (action == context.getPackageName() + ".pusheco.NOTIF_BTN_CLICKED") {
            data["event"] = "notification_button_clicked"
            EventBus.getDefault().post(MessageEvent(data.toString()))
        }
    }

}