package lk.pulsarconnect.plus

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView
    private val requestCode = 4321
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(24,24,24,24) }
        root.addView(TextView(this).apply { text="Pulsar Connect Plus"; textSize=25f })
        root.addView(TextView(this).apply {
            text="Protocol test • Honor X7b + PULSAR5438\n\nThis test checks paired-device details and captures notifications. It does NOT yet send WhatsApp text to the dashboard because the dashboard protocol is unknown."
            textSize=16f; setPadding(0,12,0,12)
        })
        root.addView(Button(this).apply {
            text="1. Allow notifications"
            setOnClickListener { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
        })
        root.addView(Button(this).apply {
            text="2. Check Bluetooth permissions"
            setOnClickListener { requestBluetoothPermissions() }
        })
        root.addView(Button(this).apply {
            text="3. Refresh test report"
            setOnClickListener { refreshReport() }
        })
        root.addView(Button(this).apply {
            text="Open Bluetooth settings"
            setOnClickListener { startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) }
        })
        status = TextView(this).apply { textSize=14f; setPadding(0,16,0,0) }
        val scroll = ScrollView(this); scroll.addView(status)
        root.addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f))
        root.gravity = Gravity.TOP
        setContentView(root)
        refreshReport()
    }
    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= 31) {
            val missing = arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
                .filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
            if (missing.isNotEmpty()) requestPermissions(missing.toTypedArray(), requestCode) else refreshReport()
        } else refreshReport()
    }
    override fun onRequestPermissionsResult(requestCode:Int, permissions:Array<out String>, grantResults:IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); refreshReport()
    }
    override fun onResume() { super.onResume(); if (::status.isInitialized) refreshReport() }
    private fun refreshReport() {
        val lines = mutableListOf<String>()
        lines += "TEST REPORT"
        lines += "Notification access: " + if ((Settings.Secure.getString(contentResolver,"enabled_notification_listeners") ?: "").contains(packageName)) "ON" else "OFF"
        val manager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = manager.adapter
        lines += "Bluetooth available: " + (adapter != null)
        if (adapter != null) {
            lines += "Bluetooth enabled: " + adapter.isEnabled
            if (Build.VERSION.SDK_INT < 31 || checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                try {
                    val paired = adapter.bondedDevices.orEmpty().sortedBy { it.name ?: it.address }
                    lines += "Paired devices (${paired.size}):"
                    paired.forEach { lines += " • ${(it.name ?: "(unnamed)")} — ${it.address}" }
                    lines += ""
                    lines += "Paired-device list only. Android does not expose the bike's supported message protocol here."
                } catch (e: SecurityException) { lines += "Bluetooth permission missing: ${e.message}" }
            } else lines += "Tap 'Check Bluetooth permissions' to allow device listing."
        }
        lines += ""
        lines += "Recent notifications:"
        lines += NotificationCaptureService.recentText()
        status.text = lines.joinToString("\n")
    }
}
