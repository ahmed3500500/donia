package com.example.islamicapp.adhan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.islamicapp.R
import com.example.islamicapp.settings.SettingsStore
import kotlinx.coroutines.runBlocking

class AdhanReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prayerName = intent?.getStringExtra("prayer_name") ?: "الصلاة"
        val prayerTime = intent?.getStringExtra("prayer_time") ?: ""

        val snap = runBlocking { SettingsStore.getSnapshot(context) }
        val enableAdhan = snap.enableAdhan
        val enableVibrate = snap.enableVibrate
        val enableNotif = snap.enablePrayerNotif

        if (enableVibrate) {
            try {
                val vib: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vm = context.getSystemService(VibratorManager::class.java)
                    vm.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }
                if (Build.VERSION.SDK_INT >= 26) {
                    vib.vibrate(VibrationEffect.createOneShot(1000L, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vib.vibrate(1000L)
                }
            } catch (_: Throwable) {
            }
        }

        if (enableAdhan) {
            val player = MediaPlayer.create(context, R.raw.adhan)
            player.setOnCompletionListener { it.release() }
            player.start()
        }

        if (enableNotif) {
            val content = if (prayerTime.isNotBlank()) "$prayerName - $prayerTime" else prayerName
            val notification = NotificationCompat.Builder(context, "prayer_adhan_channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("حان الآن وقت $prayerName")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()
            NotificationManagerCompat.from(context).notify(2001, notification)
        }
    }
}
