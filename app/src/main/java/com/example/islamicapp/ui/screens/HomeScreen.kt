package com.example.islamicapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.islamicapp.adhan.AdhanScheduler
import com.example.islamicapp.prayer.PrayerTimesUiState
import com.example.islamicapp.prayer.PrayerTimesViewModel
import com.example.islamicapp.ui.Routes
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: PrayerTimesViewModel = viewModel(),
    onNavigate: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { _ -> viewModel.tryUpdateLocationFromDevice(context) }
    )
    var now by remember { mutableStateOf(LocalDateTime.now(ZoneId.systemDefault())) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now(ZoneId.systemDefault())
            kotlinx.coroutines.delay(1000L)
        }
    }

    LaunchedEffect(Unit) {
        // طلب الصلاحيات لأول مرة (موقع + إشعارات)
        val perms = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            perms.add(Manifest.permission.ACCESS_FINE_LOCATION)
            perms.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                perms.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (perms.isNotEmpty()) {
            locationLauncher.launch(perms.toTypedArray())
        } else {
            viewModel.tryUpdateLocationFromDevice(context)
        }
    }

    LaunchedEffect(state.fajr, state.dhuhr, state.asr, state.maghrib, state.isha, state.error, state.isLoading) {
        if (!state.isLoading && state.error == null && state.fajr.isNotBlank()) {
            AdhanScheduler.scheduleAllDailyAdhans(context, state)
            com.example.islamicapp.azkar.AzkarScheduler.scheduleDaily(context, state)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF062D1A), Color(0xFF0B5B34)))
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val timeText = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val dateGregorian = now.toLocalDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))

        Text(
            text = "الوقت الآن: $timeText",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold
            )
        )
        Text(text = "التاريخ الميلادي: $dateGregorian", color = Color.White)
        if (state.hijriDate.isNotBlank()) {
            Text(text = "التاريخ الهجري: ${state.hijriDate}", color = Color.White)
        }
        if (state.locationStatus.isNotBlank()) {
            Text(text = state.locationStatus, color = Color(0xFFFFD700), fontSize = 12.sp)
        }

        PrayerSummaryCard(state = state, onRetry = { viewModel.refreshTimings() })

        HomeButtonsGrid(
            onPrayerTimes = { onNavigate(Routes.PrayerTimes) },
            onQuran = { onNavigate(Routes.Quran) },
            onTasbeeh = { onNavigate(Routes.Tasbeeh) },
            onAzkar = { onNavigate(Routes.Azkar) },
            onQibla = { onNavigate(Routes.Qibla) },
            onNames = { onNavigate(Routes.Names) }
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14402A)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "إعدادات التطبيق", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                    Text(text = "المدينة، الأذان، الإشعارات، الثيم", color = Color.White, fontSize = 12.sp)
                }
                Button(
                    onClick = { onNavigate(Routes.Settings) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text(text = "فتح", color = Color(0xFF062D1A))
                }
            }
        }
    }
}

@Composable
private fun PrayerSummaryCard(state: PrayerTimesUiState, onRetry: () -> Unit) {
    if (state.isLoading) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3B24)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(color = Color(0xFFFFD700))
                Text(text = "جاري تحميل مواقيت الصلاة", color = Color.White)
            }
        }
        return
    }
    if (state.error != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3B24)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = state.error, color = Color.White)
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text(text = "إعادة المحاولة", color = Color(0xFF062D1A))
                }
            }
        }
        return
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3B24)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "المدينة: ${state.cityArabic}", color = Color(0xFFFFD700), fontWeight = FontWeight.SemiBold)
            Text(
                text = "الصلاة القادمة: ${state.nextPrayerName} بعد ${state.nextPrayerRemaining}",
                color = Color.White,
                fontSize = 14.sp
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TimeMini(title = "الفجر", value = state.fajr)
                TimeMini(title = "الشروق", value = state.sunrise)
                TimeMini(title = "الظهر", value = state.dhuhr)
                TimeMini(title = "العصر", value = state.asr)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                TimeMini(title = "المغرب", value = state.maghrib)
                TimeMini(title = "العشاء", value = state.isha)
            }
        }
    }
}

@Composable
private fun TimeMini(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, color = Color(0xFFFFD700), fontSize = 12.sp)
        Text(text = value.ifBlank { "--:--" }, color = Color.White, fontSize = 12.sp)
    }
}

@Composable
private fun HomeButtonsGrid(
    onPrayerTimes: () -> Unit,
    onQuran: () -> Unit,
    onTasbeeh: () -> Unit,
    onAzkar: () -> Unit,
    onQibla: () -> Unit,
    onNames: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BigHomeButton(title = "مواقيت الصلاة", subtitle = "تفاصيل اليوم", onClick = onPrayerTimes, modifier = Modifier.weight(1f))
            BigHomeButton(title = "القرآن الكريم", subtitle = "سور + تلاوة", onClick = onQuran, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BigHomeButton(title = "السبحة", subtitle = "عداد + إحصائيات", onClick = onTasbeeh, modifier = Modifier.weight(1f))
            BigHomeButton(title = "الأذكار", subtitle = "صباح/مساء/...", onClick = onAzkar, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BigHomeButton(title = "القبلة", subtitle = "اتجاه + مسافة", onClick = onQibla, modifier = Modifier.weight(1f))
            BigHomeButton(title = "أسماء الله", subtitle = "99 اسماً", onClick = onNames, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(2.dp))
    }
}

@Composable
private fun BigHomeButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14402A)),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = title, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = subtitle, color = Color.White, fontSize = 12.sp)
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "فتح", color = Color(0xFF062D1A))
            }
        }
    }
}
