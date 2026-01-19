package com.example.islamicapp.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings_prefs")

/**
 * إعدادات التطبيق (DataStore)
 *
 * الهدف: تخزين كل اختيارات المستخدم (الأذان/الإشعارات/المدينة/الثيم/القارئ..)
 * بشكل ثابت حتى بعد إغلاق التطبيق.
 */
object SettingsStore {
    // --- Notifications / Adhan
    val KEY_ENABLE_ADHAN = booleanPreferencesKey("enable_adhan")
    val KEY_ENABLE_VIBRATE = booleanPreferencesKey("enable_vibrate")
    val KEY_ENABLE_PRAYER_NOTIF = booleanPreferencesKey("enable_prayer_notif")
    val KEY_ENABLE_AZKAR_NOTIF = booleanPreferencesKey("enable_azkar_notif")
    val KEY_PRE_ADHAN_MIN = intPreferencesKey("pre_adhan_min") // 0/5/10/15

    // --- Location / City
    val KEY_CITY_MODE = stringPreferencesKey("city_mode") // auto | manual
    val KEY_CITY_EN = stringPreferencesKey("city_en")
    val KEY_COUNTRY_EN = stringPreferencesKey("country_en")
    val KEY_CITY_AR = stringPreferencesKey("city_ar")
    val KEY_COUNTRY_AR = stringPreferencesKey("country_ar")

    // --- Theme / UI
    val KEY_THEME_NAME = stringPreferencesKey("theme_name") // emerald | midnight | sand | rose | ocean
    val KEY_DARK_MODE = stringPreferencesKey("dark_mode") // system | dark | light
    val KEY_FONT_SCALE = intPreferencesKey("font_scale") // 90..130 (percent)
    val KEY_LANGUAGE = stringPreferencesKey("language") // ar | en | fr | tr ... (واجهة فقط حالياً)

    // --- Quran
    val KEY_RECITER_ID = intPreferencesKey("reciter_id") // mp3quran reciter id (API)
    val KEY_RECITER_SERVER = stringPreferencesKey("reciter_server") // base server url
    val KEY_RECITER_NAME = stringPreferencesKey("reciter_name")

    data class SettingsSnapshot(
        val enableAdhan: Boolean = true,
        val enableVibrate: Boolean = true,
        val enablePrayerNotif: Boolean = true,
        val enableAzkarNotif: Boolean = true,
        val preAdhanMin: Int = 0,
        val cityMode: String = "auto",
        val cityEn: String = "",
        val countryEn: String = "",
        val cityAr: String = "",
        val countryAr: String = "",
        val themeName: String = "emerald",
        val darkMode: String = "system",
        val fontScale: Int = 100,
        val language: String = "ar",
        val reciterId: Int = 0,
        val reciterServer: String = "",
        val reciterName: String = ""
    )

    fun flow(context: Context): Flow<SettingsSnapshot> {
        return context.settingsDataStore.data.map { prefs ->
            SettingsSnapshot(
                enableAdhan = prefs[KEY_ENABLE_ADHAN] ?: true,
                enableVibrate = prefs[KEY_ENABLE_VIBRATE] ?: true,
                enablePrayerNotif = prefs[KEY_ENABLE_PRAYER_NOTIF] ?: true,
                enableAzkarNotif = prefs[KEY_ENABLE_AZKAR_NOTIF] ?: true,
                preAdhanMin = prefs[KEY_PRE_ADHAN_MIN] ?: 0,
                cityMode = prefs[KEY_CITY_MODE] ?: "auto",
                cityEn = prefs[KEY_CITY_EN] ?: "",
                countryEn = prefs[KEY_COUNTRY_EN] ?: "",
                cityAr = prefs[KEY_CITY_AR] ?: "",
                countryAr = prefs[KEY_COUNTRY_AR] ?: "",
                themeName = prefs[KEY_THEME_NAME] ?: "emerald",
                darkMode = prefs[KEY_DARK_MODE] ?: "system",
                fontScale = prefs[KEY_FONT_SCALE] ?: 100,
                language = prefs[KEY_LANGUAGE] ?: "ar",
                reciterId = prefs[KEY_RECITER_ID] ?: 0,
                reciterServer = prefs[KEY_RECITER_SERVER] ?: "",
                reciterName = prefs[KEY_RECITER_NAME] ?: ""
            )
        }
    }

    suspend fun getSnapshot(context: Context): SettingsSnapshot {
        return flow(context).first()
    }

    suspend fun setBoolean(context: Context, key: Preferences.Key<Boolean>, value: Boolean) {
        context.settingsDataStore.edit { it[key] = value }
    }

    suspend fun setInt(context: Context, key: Preferences.Key<Int>, value: Int) {
        context.settingsDataStore.edit { it[key] = value }
    }

    suspend fun setString(context: Context, key: Preferences.Key<String>, value: String) {
        context.settingsDataStore.edit { it[key] = value }
    }
}
