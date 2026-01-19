package com.example.islamicapp.adhan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.islamicapp.prayer.PrayerTimesUiState
import java.util.Calendar

private fun parseHm(time: String): Pair<Int, Int>? {
    val p = time.take(5).split(":")
    if (p.size != 2) return null
    val h = p[0].toIntOrNull() ?: return null
    val m = p[1].toIntOrNull() ?: return null
    return h to m
}

object AdhanScheduler {
    fun scheduleNextAdhan(context: Context, diffMinutes: Int) {
        if (diffMinutes <= 0) return
        val triggerAtMillis = System.currentTimeMillis() + diffMinutes * 60_000L
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AdhanReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        manager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pending
        )
    }

    /**
     * جدولة الأذان ...
     */
    fun scheduleAllDailyAdhans(context: Context, state: PrayerTimesUiState) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val items = listOf(
            Triple(2001, "الفجر", state.fajr),
            Triple(2002, "الظهر", state.dhuhr),
            Triple(2003, "العصر", state.asr),
            Triple(2004, "المغرب", state.maghrib),
            Triple(2005, "العشاء", state.isha)
        )

        val now = Calendar.getInstance()

        items.forEach { (requestCode, name, timeStr) ->
            val hm = parseHm(timeStr) ?: return@forEach
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hm.first)
                set(Calendar.MINUTE, hm.second)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (cal.timeInMillis <= now.timeInMillis + 5_000L) {
                cal.add(Calendar.DATE, 1)
            }

            val intent = Intent(context, AdhanReceiver::class.java).apply {
                putExtra("prayer_name", name)
                putExtra("prayer_time", timeStr)
            }
            val pending = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pending)
        }
    }
}

