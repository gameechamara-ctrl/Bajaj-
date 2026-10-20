package lk.pulsar.whatsappbridge

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import org.json.JSONObject

class WhatsAppNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != "com.whatsapp" && sbn.packageName != "com.whatsapp.w4b") return
        val title = sbn.notification.extras.getCharSequence("android.title")?.toString().orEmpty()
        // Intentionally store and send only the sender/title, never the message body.
        if (title.isBlank()) return
        val payload = JSONObject().put("title", title).put("time", System.currentTimeMillis()).toString()
        getSharedPreferences("bridge", MODE_PRIVATE).edit().putString("latest", payload).apply()
        MainActivity.sendLatestIfConnected(title)
    }
}
