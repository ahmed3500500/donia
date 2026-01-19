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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.islamicapp.tasbeeh.TasbeehStats
import com.example.islamicapp.tasbeeh.TasbeehStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TasbeehScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var count by remember { mutableIntStateOf(0) }
    var stats by remember { mutableStateOf(TasbeehStats()) }

    LaunchedEffect(Unit) {
        count = withContext(Dispatchers.IO) { TasbeehStore.getCount(context) }
        stats = withContext(Dispatchers.IO) { TasbeehStore.getStats(context) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF062D1A), Color(0xFF0B5B34))))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("السبحة", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3B24)),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("العدد الحالي", color = Color.White, fontSize = 13.sp)
                Text(count.toString(), color = Color(0xFFFFD700), fontSize = 44.sp, fontWeight = FontWeight.Bold)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            count += 1
                            TasbeehStore.increment(context)
                            // تحديث سريع
                            stats = TasbeehStore.getStats(context)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                    ) { Text("+1", color = Color(0xFF062D1A), fontWeight = FontWeight.Bold) }

                    Button(
                        onClick = {
                            count = 0
                            TasbeehStore.resetToday(context)
                            stats = TasbeehStore.getStats(context)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14402A))
                    ) { Text("تصفير اليوم", color = Color.White) }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14402A)),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("إحصائيات", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                StatRow("اليوم", stats.today)
                StatRow("هذا الأسبوع", stats.week)
                StatRow("هذا الشهر", stats.month)
                Spacer(Modifier.height(4.dp))
                Text("ملاحظة: الإحصائيات تُحسب محلياً داخل التطبيق.", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White)
        Text(value.toString(), color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
    }
}
