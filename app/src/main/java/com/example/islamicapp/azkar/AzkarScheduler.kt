package com.example.islamicapp.azkar

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.islamicapp.prayer.PrayerTimesUiState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object AzkarScheduler {
    private val fmt = DateTimeFormatter.ofPattern("HH:mm")

    /**
     * 3 تذكيرات يومياً:
     * - أذكار الصباح: بعد الفجر (5 دقائق)
     * - أذكار المساء: بعد العصر (5 دقائق)
     * - أذكار النوم: 10:00 مساءً
     */
    fun scheduleDaily(context: Context, state: PrayerTimesUiState) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        scheduleOne(
            context,
            alarm,
            requestCode = 4001,
            title = "أذكار الصباح",
            body = "خذ دقيقة لأذكار الصباح",
            at = addMinutesSafe(todayAt(state.fajr), 5)
        )

        scheduleOne(
            context,
            alarm,
            requestCode = 4002,
            title = "أذكار المساء",
            body = "خذ دقيقة لأذكار المساء",
            at = addMinutesSafe(todayAt(state.asr), 5)
        )

        // 10:00 PM
        val sleepAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 0))
        scheduleOne(
            context,
            alarm,
            requestCode = 4003,
            title = "أذكار النوم",
            body = "قبل النوم… لا تنس الأذكار",
            at = nextIfPast(sleepAt)
        )
    }

    private fun todayAt(hhmm: String): LocalDateTime {
        return try {
            val t = LocalTime.parse(hhmm.trim(), fmt)
            LocalDateTime.of(LocalDate.now(), t)
        } catch (_: Throwable) {
            // fallback: 08:00 AM
            LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0))
        }
    }

    private fun addMinutesSafe(dt: LocalDateTime, minutes: Long): LocalDateTime {
        return nextIfPast(dt.plusMinutes(minutes))
    }

    private fun nextIfPast(dt: LocalDateTime): LocalDateTime {
        val now = LocalDateTime.now()
        return if (dt.isAfter(now)) dt else dt.plusDays(1)
    }

    private fun scheduleOne(
        context: Context,
        alarm: AlarmManager,
        requestCode: Int,
        title: String,
        body: String,
        at: LocalDateTime
    ) {
        val intent = Intent(context, AzkarReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("body", body)
        }
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val triggerAt = at.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarm.canScheduleExactAlarms()) {
                // لو النظام منع exact alarms: نستخدم inexact كحل عملي
                alarm.set(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                return
            }
        }
        alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
    }
}
