package lk.pulsarconnect.plus

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationCaptureService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = (extras.getCharSequence(Notification.EXTRA_BIG_TEXT) ?: extras.getCharSequence(Notification.EXTRA_TEXT))?.toString().orEmpty()
        val app = when (sbn.packageName) {
            "com.whatsapp" -> "WhatsApp"
            "com.google.android.apps.messaging" -> "SMS"
            "com.android.dialer", "com.google.android.dialer" -> "Phone"
            else -> sbn.packageName
        }
        val line = "${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())} • $app\n$title\n$text"
        synchronized(lock) {
            entries.add(0, line.take(500))
            while (entries.size > 30) entries.removeAt(entries.lastIndex)
        }
    }
    companion object {
        private val lock = Any()
        private val entries = mutableListOf<String>()
        fun recentText(): String = synchronized(lock) {
            if (entries.isEmpty()) "No notifications captured yet. Enable notification access and receive a test message."
            else entries.joinToString("\n\n--------------------\n\n")
        }
    }
}
