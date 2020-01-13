package co.avalinejad.iq.receiver

import co.avalinejad.iq.eventbus.MessageEvent
import com.pushpole.sdk.PushPoleListenerService
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.util.HashMap

class NotificationListener: PushPoleListenerService(){

    override fun onMessageReceived(message: JSONObject?, content: JSONObject?) {
        val data = HashMap<String, String>()
        data["event"] = "notification_received"
        data["notification"] = content.toString()
        if (message != null && !message.toString().isEmpty()) {
            data["custom_content"] = content.toString()
        }
        // Send to who wants it
        EventBus.getDefault().post(MessageEvent(data.toString()))
    }

}