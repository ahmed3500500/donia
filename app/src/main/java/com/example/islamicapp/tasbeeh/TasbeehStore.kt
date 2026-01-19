package com.example.islamicapp.tasbeeh

import android.content.Context
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

/**
 * تخزين بسيط للسبحة + إحصائيات (اليوم/الأسبوع/الشهر) باستخدام SharedPreferences.
 */
object TasbeehStore {
    private const val PREF = "tasbeeh"
    private const val KEY_COUNT = "count"
    private const val KEY_DAY = "day"
    private const val KEY_WEEK = "week"
    private const val KEY_MONTH = "month"
    private const val KEY_TODAY_TOTAL = "today_total"
    private const val KEY_WEEK_TOTAL = "week_total"
    private const val KEY_MONTH_TOTAL = "month_total"

    private fun nowKeys(): Triple<String, String, String> {
        val today = LocalDate.now()
        val week = today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
        val weekKey = "${today.year}-W$week"
        val monthKey = "${today.year}-${today.monthValue}"
        val dayKey = today.toString()
        return Triple(dayKey, weekKey, monthKey)
    }

    private fun ensureBuckets(ctx: Context) {
        val p = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val (dayKey, weekKey, monthKey) = nowKeys()
        val storedDay = p.getString(KEY_DAY, "") ?: ""
        val storedWeek = p.getString(KEY_WEEK, "") ?: ""
        val storedMonth = p.getString(KEY_MONTH, "") ?: ""
        val e = p.edit()
        if (storedDay != dayKey) {
            e.putString(KEY_DAY, dayKey)
            e.putInt(KEY_TODAY_TOTAL, 0)
        }
        if (storedWeek != weekKey) {
            e.putString(KEY_WEEK, weekKey)
            e.putInt(KEY_WEEK_TOTAL, 0)
        }
        if (storedMonth != monthKey) {
            e.putString(KEY_MONTH, monthKey)
            e.putInt(KEY_MONTH_TOTAL, 0)
        }
        e.apply()
    }

    fun getCount(ctx: Context): Int {
        ensureBuckets(ctx)
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).getInt(KEY_COUNT, 0)
    }

    fun increment(ctx: Context) {
        ensureBuckets(ctx)
        val p = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val e = p.edit()
        e.putInt(KEY_COUNT, p.getInt(KEY_COUNT, 0) + 1)
        e.putInt(KEY_TODAY_TOTAL, p.getInt(KEY_TODAY_TOTAL, 0) + 1)
        e.putInt(KEY_WEEK_TOTAL, p.getInt(KEY_WEEK_TOTAL, 0) + 1)
        e.putInt(KEY_MONTH_TOTAL, p.getInt(KEY_MONTH_TOTAL, 0) + 1)
        e.apply()
    }

    fun resetToday(ctx: Context) {
        ensureBuckets(ctx)
        val p = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        p.edit().putInt(KEY_COUNT, 0).putInt(KEY_TODAY_TOTAL, 0).apply()
    }

    fun getStats(ctx: Context): TasbeehStats {
        ensureBuckets(ctx)
        val p = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return TasbeehStats(
            today = p.getInt(KEY_TODAY_TOTAL, 0),
            week = p.getInt(KEY_WEEK_TOTAL, 0),
            month = p.getInt(KEY_MONTH_TOTAL, 0)
        )
    }
}

data class TasbeehStats(
    val today: Int = 0,
    val week: Int = 0,
    val month: Int = 0
)
