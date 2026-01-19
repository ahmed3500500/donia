package com.example.islamicapp.prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.islamicapp.data.PrayerTimesRepository
import com.example.islamicapp.util.LocationUtils
import android.content.Context
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.abs

data class PrayerTimesUiState(
    val cityArabic: String = "مكة المكرمة",
    val cityEnglish: String = "Makkah",
    val countryEnglish: String = "Saudi Arabia",
    val hijriDate: String = "",
    val gregorianDate: String = "",
    val fajr: String = "",
    val sunrise: String = "",
    val dhuhr: String = "",
    val asr: String = "",
    val maghrib: String = "",
    val isha: String = "",
    val nextPrayerName: String = "",
    val nextPrayerTime: String = "",
    val nextPrayerRemaining: String = "",
    val nextPrayerDiffMinutes: Int = 0,
    val nextPrayerDiffSeconds: Int = 0,
    val locationStatus: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

class PrayerTimesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PrayerTimesUiState())
    val uiState: StateFlow<PrayerTimesUiState> = _uiState

    private var timerJob: Job? = null

    init {
        refreshTimings()
    }

    fun tryUpdateLocationFromDevice(context: Context) {
        viewModelScope.launch {
            val cc = LocationUtils.getCityAndCountry(context)
            if (cc == null) {
                _uiState.value = _uiState.value.copy(locationStatus = "لم يتم الحصول على الموقع بعد… فعّل GPS وامنح صلاحية الموقع")
                return@launch
            }
            // إذا تغيرت المدينة، حدث المواقيت
            val current = _uiState.value
            val changed = cc.city != current.cityEnglish || cc.country != current.countryEnglish
            _uiState.value = current.copy(
                cityArabic = cc.cityArabic.ifBlank { cc.city },
                cityEnglish = cc.city.ifBlank { current.cityEnglish },
                countryEnglish = cc.country.ifBlank { current.countryEnglish },
                locationStatus = "تم تحديد الموقع: ${cc.cityArabic.ifBlank { cc.city }}"
            )
            if (changed) refreshTimings()
        }
    }

    fun refreshTimings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = PrayerTimesRepository.getTimingsByCity(
                    _uiState.value.cityEnglish,
                    _uiState.value.countryEnglish
                )
                val data = response.data
                val timings = data?.timings
                val hijri = data?.date?.hijri?.date.orEmpty()
                val greg = data?.date?.gregorian?.date.orEmpty()
                val newState = _uiState.value.copy(
                    hijriDate = hijri,
                    gregorianDate = greg,
                    fajr = timings?.fajr.orEmpty(),
                    sunrise = timings?.sunrise.orEmpty(),
                    dhuhr = timings?.dhuhr.orEmpty(),
                    asr = timings?.asr.orEmpty(),
                    maghrib = timings?.maghrib.orEmpty(),
                    isha = timings?.isha.orEmpty(),
                    isLoading = false
                )
                _uiState.value = newState
                updateNextPrayer()
                startTimer()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "تعذر تحميل مواقيت الصلاة"
                )
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1_000L)
                updateNextPrayer()
            }
        }
    }

    private fun parseTimeToMinutes(time: String): Int? {
        val parts = time.take(5).split(":")
        if (parts.size != 2) return null
        val hour = parts[0].toIntOrNull() ?: return null
        val minute = parts[1].toIntOrNull() ?: return null
        return hour * 60 + minute
    }

    private fun updateNextPrayer() {
        val state = _uiState.value
        val now = Calendar.getInstance()
        val nowSeconds = now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND)

        val times = listOf(
            Triple("الفجر", state.fajr, parseTimeToMinutes(state.fajr)),
            Triple("الظهر", state.dhuhr, parseTimeToMinutes(state.dhuhr)),
            Triple("العصر", state.asr, parseTimeToMinutes(state.asr)),
            Triple("المغرب", state.maghrib, parseTimeToMinutes(state.maghrib)),
            Triple("العشاء", state.isha, parseTimeToMinutes(state.isha))
        ).filter { it.third != null }

        if (times.isEmpty()) return

        val next = times.minByOrNull { triple ->
            val minutes = triple.third!!
            val targetSeconds = minutes * 60
            val diff = when {
                targetSeconds >= nowSeconds -> targetSeconds - nowSeconds
                else -> targetSeconds + 24 * 3600 - nowSeconds
            }
            if (diff == 0) 24 * 3600 else diff
        } ?: return

        val nextMinutes = next.third!!
        val targetSeconds = nextMinutes * 60
        var diffSeconds = targetSeconds - nowSeconds
        if (diffSeconds <= 0) diffSeconds += 24 * 3600
        val diffMinutes = (diffSeconds + 59) / 60
        val h = diffSeconds / 3600
        val m = (diffSeconds % 3600) / 60
        val s = abs(diffSeconds % 60)
        val remaining = String.format("%02d:%02d:%02d", h, m, s)

        _uiState.value = state.copy(
            nextPrayerName = next.first,
            nextPrayerTime = next.second,
            nextPrayerRemaining = remaining,
            nextPrayerDiffMinutes = diffMinutes
            ,nextPrayerDiffSeconds = diffSeconds
        )
    }
}
