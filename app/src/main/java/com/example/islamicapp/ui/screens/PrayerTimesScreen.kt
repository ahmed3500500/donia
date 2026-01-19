package com.example.islamicapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.islamicapp.prayer.PrayerTimesViewModel

@Composable
fun PrayerTimesScreen(
    modifier: Modifier = Modifier,
    viewModel: PrayerTimesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF062D1A), Color(0xFF0B5B34))))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "مواقيت الصلاة",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3B24)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "المدينة: ${state.cityArabic}", color = Color.White)
                if (state.gregorianDate.isNotBlank()) {
                    Text(text = "الميلادي: ${state.gregorianDate}", color = Color.White, fontSize = 12.sp)
                }
                if (state.hijriDate.isNotBlank()) {
                    Text(text = "الهجري: ${state.hijriDate}", color = Color.White, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "الصلاة القادمة: ${state.nextPrayerName} (${state.nextPrayerTime}) بعد ${state.nextPrayerRemaining}",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        PrayerTimesTable(
            nextPrayer = state.nextPrayerName,
            fajr = state.fajr,
            sunrise = state.sunrise,
            dhuhr = state.dhuhr,
            asr = state.asr,
            maghrib = state.maghrib,
            isha = state.isha
        )
    }
}

@Composable
private fun PrayerTimesTable(
    nextPrayer: String,
    fajr: String,
    sunrise: String,
    dhuhr: String,
    asr: String,
    maghrib: String,
    isha: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        PrayerRow(title = "الفجر", time = fajr, highlight = nextPrayer == "الفجر")
        PrayerRow(title = "الشروق", time = sunrise, highlight = false)
        PrayerRow(title = "الظهر", time = dhuhr, highlight = nextPrayer == "الظهر")
        PrayerRow(title = "العصر", time = asr, highlight = nextPrayer == "العصر")
        PrayerRow(title = "المغرب", time = maghrib, highlight = nextPrayer == "المغرب")
        PrayerRow(title = "العشاء", time = isha, highlight = nextPrayer == "العشاء")
    }
}

@Composable
private fun PrayerRow(title: String, time: String, highlight: Boolean) {
    val bg = if (highlight) Color(0xFF1C6B46) else Color(0xFF14402A)
    val fgTitle = if (highlight) Color(0xFFFFD700) else Color.White

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, color = fgTitle, fontWeight = FontWeight.Bold)
            Text(text = time.ifBlank { "--:--" }, color = Color.White)
        }
    }
}
