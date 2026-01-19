package com.example.islamicapp.azkar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.islamicapp.R
import com.example.islamicapp.settings.SettingsStore
import kotlinx.coroutines.runBlocking

class AzkarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val snap = runBlocking { SettingsStore.getSnapshot(context) }
        if (!snap.enableAzkarNotif) return

        val title = intent?.getStringExtra("title") ?: "تذكير بالأذكار"
        val body = intent?.getStringExtra("body") ?: "لا تنس أذكارك"

        if (snap.enableVibrate) {
            try {
                val vib: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vm = context.getSystemService(VibratorManager::class.java)
                    vm.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }
                if (Build.VERSION.SDK_INT >= 26) {
                    vib.vibrate(VibrationEffect.createOneShot(600L, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vib.vibrate(600L)
                }
            } catch (_: Throwable) {
            }
        }

        val notification = NotificationCompat.Builder(context, "azkar_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(3101, notification)
    }
}
