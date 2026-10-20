package lk.pulsar.whatsappbridge

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import org.json.JSONObject
import java.io.OutputStream
import java.util.UUID
import kotlin.concurrent.thread

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var latest: TextView
    private val adapter: BluetoothAdapter? get() = BluetoothAdapter.getDefaultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(28, 28, 28, 28) }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val heading = TextView(this).apply { text = "Pulsar WhatsApp Bridge"; textSize = 24f }
        status = TextView(this).apply { textSize = 16f; text = "Status: ready" }
        latest = TextView(this).apply { textSize = 16f; text = "Latest WhatsApp notification: none yet" }
        content.addView(heading)
        content.addView(TextView(this).apply { text = "Shows the WhatsApp sender name only. Dashboard compatibility is not confirmed."; textSize = 14f })
        content.addView(status)
        content.addView(latest)
        content.addView(btn("Enable notification access") { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) })
        content.addView(btn("Choose paired Bluetooth device") { chooseDevice() })
        content.addView(btn("Send test message to connected device") { sendText("PULSAR BRIDGE TEST") })
        content.addView(btn("Refresh latest notification") { refreshLatest() })
        content.addView(btn("Bluetooth settings") { startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) })
        scroll.addView(content)
        root.addView(scroll)
        setContentView(root)
        askPermissions()
        refreshLatest()
    }

    private fun btn(label: String, action: () -> Unit) = Button(this).apply {
        text = label; setOnClickListener { action() }; layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun askPermissions() {
        if (Build.VERSION.SDK_INT >= 31 && checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 42)
        }
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 43)
        }
    }

    private fun chooseDevice() {
        if (Build.VERSION.SDK_INT >= 31 && checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            status.text = "Allow Nearby devices permission first."; askPermissions(); return
        }
        val a = adapter
        if (a == null) { status.text = "This phone has no Bluetooth adapter."; return }
        if (!a.isEnabled) { startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)); return }
        val devices = try { a.bondedDevices.toList() } catch (_: SecurityException) { emptyList() }
        if (devices.isEmpty()) { status.text = "No paired devices. Pair the bike/device in Android Bluetooth settings first."; return }
        AlertDialog.Builder(this).setTitle("Paired Bluetooth devices")
            .setItems(devices.map { "${it.name ?: "Unknown"} — ${it.address}" }.toTypedArray()) { _, which ->
                connect(devices[which])
            }.setNegativeButton("Cancel", null).show()
    }

    private fun connect(device: BluetoothDevice) {
        thread {
            try {
                if (Build.VERSION.SDK_INT >= 31 && checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) throw SecurityException("Bluetooth permission missing")
                val socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                adapter?.cancelDiscovery()
                socket.connect()
                btSocket = socket
                runOnUiThread { status.text = "Connected to ${device.name ?: device.address} via Bluetooth SPP. Dashboard protocol still needs verification." }
            } catch (e: Exception) {
                runOnUiThread { status.text = "Connection failed: ${e.message}. The bike may use a proprietary BLE protocol rather than Bluetooth SPP." }
            }
        }
    }

    private fun sendText(text: String) {
        thread {
            try {
                val out = btSocket?.outputStream ?: throw IllegalStateException("Connect a paired device first")
                out.write((text.replace("\n", " ") + "\n").toByteArray(Charsets.UTF_8)); out.flush()
                runOnUiThread { status.text = "Test bytes sent. This does not confirm dashboard understands the format." }
            } catch (e: Exception) { runOnUiThread { status.text = "Send failed: ${e.message}" } }
        }
    }

    private fun refreshLatest() {
        val raw = getSharedPreferences("bridge", MODE_PRIVATE).getString("latest", null)
        latest.text = if (raw == null) "Latest WhatsApp sender: none yet" else try {
            val o = JSONObject(raw); "Latest WhatsApp sender:\n${o.optString("title")}"
        } catch (_: Exception) { raw }
    }

    companion object {
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        @Volatile private var btSocket: BluetoothSocket? = null
        fun sendLatestIfConnected(title: String) {
            // Experimental generic SPP format; the Pulsar dashboard's actual protocol is not known.
            thread {
                try {
                    val out: OutputStream = btSocket?.outputStream ?: return@thread
                    val safeTitle = title.replace('|', ' ').replace('\n', ' ')
                    val payload = "WA|$safeTitle\n"
                    out.write(payload.toByteArray(Charsets.UTF_8)); out.flush()
                } catch (_: Exception) { }
            }
        }
    }
}
