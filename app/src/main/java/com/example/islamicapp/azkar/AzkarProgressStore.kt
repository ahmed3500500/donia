package com.example.islamicapp.azkar

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.azkarDataStore by preferencesDataStore(name = "azkar_progress")

object AzkarProgressStore {
    private val KEY_MORNING = intPreferencesKey("azkar_morning_index")
    private val KEY_EVENING = intPreferencesKey("azkar_evening_index")
    private val KEY_SLEEP = intPreferencesKey("azkar_sleep_index")

    suspend fun getIndex(context: Context, type: AzkarType): Int {
        val prefs = context.azkarDataStore.data.first()
        return when (type) {
            AzkarType.MORNING -> prefs[KEY_MORNING] ?: 0
            AzkarType.EVENING -> prefs[KEY_EVENING] ?: 0
            AzkarType.SLEEP -> prefs[KEY_SLEEP] ?: 0
        }
    }

    suspend fun setIndex(context: Context, type: AzkarType, index: Int) {
        context.azkarDataStore.edit { prefs ->
            when (type) {
                AzkarType.MORNING -> prefs[KEY_MORNING] = index
                AzkarType.EVENING -> prefs[KEY_EVENING] = index
                AzkarType.SLEEP -> prefs[KEY_SLEEP] = index
            }
        }
    }
}

enum class AzkarType { MORNING, EVENING, SLEEP }
