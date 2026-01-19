package com.example.islamicapp.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.islamicapp.util.LocationUtils
import com.example.islamicapp.util.QiblaMath
import kotlinx.coroutines.launch

@Composable
fun QiblaScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("جارٍ تحديد موقعك...") }
    var city by remember { mutableStateOf("") }
    var bearing by remember { mutableStateOf<Double?>(null) }
    var distanceKm by remember { mutableStateOf<Double?>(null) }
    var azimuthDeg by remember { mutableStateOf(0f) }

    // Compass sensor (Rotation Vector)
    DisposableEffect(Unit) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotation = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotation == null) {
            onDispose { }
        } else {
            val listener = object : SensorEventListener {
                private val rot = FloatArray(9)
                private val orient = FloatArray(3)
                override fun onSensorChanged(event: SensorEvent) {
                    SensorManager.getRotationMatrixFromVector(rot, event.values)
                    SensorManager.getOrientation(rot, orient)
                    // azimuth in radians -> degrees
                    var deg = Math.toDegrees(orient[0].toDouble()).toFloat()
                    if (deg < 0) deg += 360f
                    azimuthDeg = deg
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            sm.registerListener(listener, rotation, SensorManager.SENSOR_DELAY_UI)
            onDispose { sm.unregisterListener(listener) }
        }
    }

    suspend fun refresh() {
        val loc = LocationUtils.getLatLng(context)
        if (loc == null) {
            status = "لم يتمكن التطبيق من تحديد موقعك (تأكد من صلاحيات الموقع)"
            return
        }
        val cc = LocationUtils.getCityAndCountry(context)
        city = cc?.cityArabic.orEmpty()
        val result = QiblaMath.bearingAndDistanceToKaaba(loc.first, loc.second)
        bearing = result.first
        distanceKm = result.second
        status = "تم تحديد الموقع"
    }

    LaunchedEffect(Unit) { refresh() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF062D1A), Color(0xFF0B5B34))))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "القبلة",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold
            )
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3B24)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = status, color = Color.White)
                if (city.isNotBlank()) {
                    Text(text = "المدينة: $city", color = Color(0xFFFFD700))
                }
                if (bearing != null) {
                    Text(text = "اتجاه القبلة (بالدرجات): ${String.format("%.1f", bearing)}°", color = Color.White)
                }
                if (distanceKm != null) {
                    Text(text = "المسافة إلى مكة: ${String.format("%.0f", distanceKm)} كم", color = Color.White)
                }

                Spacer(Modifier.height(6.dp))
                val target = (bearing ?: 0.0).toFloat()
                val delta = abs(((target - azimuthDeg + 540f) % 360f) - 180f)
                Text(
                    text = "اتجاهك الحالي: ${azimuthDeg.roundToInt()}° — الفرق: ${delta.roundToInt()}°",
                    color = Color.White,
                    fontSize = 12.sp
                )

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val radius = minOf(size.width, size.height) * 0.38f

                    // circle
                    drawCircle(color = Color(0xFF14402A), radius = radius * 1.25f, center = androidx.compose.ui.geometry.Offset(cx, cy))
                    drawCircle(color = Color(0xFF0B5B34), radius = radius * 1.05f, center = androidx.compose.ui.geometry.Offset(cx, cy))

                    // needle to Qibla
                    val angle = Math.toRadians((target - azimuthDeg).toDouble())
                    val x = cx + (radius * kotlin.math.sin(angle)).toFloat()
                    val y = cy - (radius * kotlin.math.cos(angle)).toFloat()
                    drawLine(
                        color = if (delta <= 5f) Color(0xFF00E676) else Color(0xFFFFD700),
                        start = androidx.compose.ui.geometry.Offset(cx, cy),
                        end = androidx.compose.ui.geometry.Offset(x, y),
                        strokeWidth = 10f
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 10f,
                        center = androidx.compose.ui.geometry.Offset(cx, cy)
                    )
                }

                if (delta <= 5f) {
                    Text("✅ أنت على اتجاه القبلة تقريباً", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                } else {
                    Text("حرّك الهاتف حتى يصبح السهم أخضر", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        Button(
            onClick = { scope.launch { refresh() } },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "تحديث", color = Color(0xFF062D1A))
        }
    }
}
