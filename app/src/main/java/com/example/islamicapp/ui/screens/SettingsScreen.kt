package com.example.islamicapp.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.islamicapp.settings.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var enableAdhan by remember { mutableStateOf(true) }
    var enableVibrate by remember { mutableStateOf(true) }
    var enablePrayerNotifications by remember { mutableStateOf(true) }
    var enableAzkarNotifications by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val snap = SettingsStore.getSnapshot(context)
        enableAdhan = snap.enableAdhan
        enableVibrate = snap.enableVibrate
        enablePrayerNotifications = snap.enablePrayerNotifications
        enableAzkarNotifications = snap.enableAzkarNotifications
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF062D1A), Color(0xFF0B5B34))))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "الإعدادات",
            color = Color(0xFFFFD700),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        SettingSwitchCard(
            title = "تشغيل الأذان",
            subtitle = "تفعيل/تعطيل تشغيل صوت الأذان عند وقت الصلاة",
            checked = enableAdhan,
            onCheckedChange = {
                enableAdhan = it
                saveBoolean(context, SettingsStore.KEY_ENABLE_ADHAN, it)
            }
        )

        SettingSwitchCard(
            title = "اهتزاز مع الإشعار",
            subtitle = "اهتزاز بسيط مع تنبيه الأذان",
            checked = enableVibrate,
            onCheckedChange = {
                enableVibrate = it
                saveBoolean(context, SettingsStore.KEY_ENABLE_VIBRATE, it)
            }
        )

        SettingSwitchCard(
            title = "إشعارات الصلوات",
            subtitle = "إظهار إشعار عند دخول وقت الصلاة (حتى لو الأذان مغلق)",
            checked = enablePrayerNotifications,
            onCheckedChange = {
                enablePrayerNotifications = it
                saveBoolean(context, SettingsStore.KEY_ENABLE_PRAYER_NOTIFICATIONS, it)
            }
        )

        SettingSwitchCard(
            title = "تنبيهات الأذكار اليومية",
            subtitle = "تذكير بعد الفجر وبعد العصر ووقت النوم",
            checked = enableAzkarNotifications,
            onCheckedChange = {
                enableAzkarNotifications = it
                saveBoolean(context, SettingsStore.KEY_ENABLE_AZKAR_NOTIFICATIONS, it)
            }
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14402A)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "ملاحظة", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                Text(
                    text = "تحديد المدينة يتم تلقائياً عند فتح التطبيق (حسب صلاحيات الموقع). لو ما اشتغل، افتح GPS واسمح بالموقع.",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "حفظ", color = Color(0xFF062D1A), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SettingSwitchCard(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = Color.White, fontSize = 12.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700))
            )
        }
    }
}

private fun saveBoolean(context: Context, key: androidx.datastore.preferences.core.Preferences.Key<Boolean>, value: Boolean) {
    CoroutineScope(Dispatchers.IO).launch {
        SettingsStore.setBoolean(context, key, value)
    }
}
