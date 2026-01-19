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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.islamicapp.azkar.AzkarProgressStore
import com.example.islamicapp.azkar.AzkarType
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DhikrItem(val text: String, val count: Int)

@Composable
fun AzkarScreen(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    // ملاحظة: هذه قاعدة بسيطة كبداية. يمكنك توسيعها لاحقاً (إضافة 100+ ذكر لكل قسم).
    val morning = remember {
        listOf(
            DhikrItem("أصبحنا وأصبح الملك لله...", 1),
            DhikrItem("اللهم بك أصبحنا وبك أمسينا...", 1),
            DhikrItem("سبحان الله وبحمده", 100),
            DhikrItem("لا إله إلا الله وحده لا شريك له...", 100)
        )
    }
    val evening = remember {
        listOf(
            DhikrItem("أمسينا وأمسى الملك لله...", 1),
            DhikrItem("اللهم بك أمسينا وبك أصبحنا...", 1),
            DhikrItem("أعوذ بكلمات الله التامات من شر ما خلق", 3),
            DhikrItem("سبحان الله وبحمده", 100)
        )
    }
    val sleep = remember {
        listOf(
            DhikrItem("باسمك اللهم أموت وأحيا", 1),
            DhikrItem("اللهم قني عذابك يوم تبعث عبادك", 3),
            DhikrItem("سبحان الله (33) الحمد لله (33) الله أكبر (34)", 1),
            DhikrItem("آية الكرسي", 1)
        )
    }

    var type by remember { mutableStateOf(AzkarType.MORNING) }
    var index by remember { mutableIntStateOf(0) }

    val list = when (type) {
        AzkarType.MORNING -> morning
        AzkarType.EVENING -> evening
        AzkarType.SLEEP -> sleep
    }

    LaunchedEffect(type) {
        index = withContext(Dispatchers.IO) { AzkarProgressStore.getIndex(context, type) }
        if (index !in list.indices) index = 0
    }

    fun persist(newIndex: Int) {
        index = newIndex
        scope.launch(Dispatchers.IO) {
            AzkarProgressStore.setIndex(context, type, newIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF062D1A), Color(0xFF0B5B34))))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "الأذكار",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold
            )
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { type = AzkarType.MORNING },
                colors = ButtonDefaults.buttonColors(containerColor = if (type == AzkarType.MORNING) Color(0xFFFFD700) else Color(0xFF14402A))
            ) { Text("الصباح", color = if (type == AzkarType.MORNING) Color(0xFF062D1A) else Color.White) }

            Button(
                onClick = { type = AzkarType.EVENING },
                colors = ButtonDefaults.buttonColors(containerColor = if (type == AzkarType.EVENING) Color(0xFFFFD700) else Color(0xFF14402A))
            ) { Text("المساء", color = if (type == AzkarType.EVENING) Color(0xFF062D1A) else Color.White) }

            Button(
                onClick = { type = AzkarType.SLEEP },
                colors = ButtonDefaults.buttonColors(containerColor = if (type == AzkarType.SLEEP) Color(0xFFFFD700) else Color(0xFF14402A))
            ) { Text("النوم", color = if (type == AzkarType.SLEEP) Color(0xFF062D1A) else Color.White) }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14402A)),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = list.getOrNull(index)?.text ?: "",
                    color = Color.White,
                    fontSize = 18.sp,
                    lineHeight = 26.sp
                )
                Text(
                    text = "التكرار: ${list.getOrNull(index)?.count ?: 0}",
                    color = Color(0xFFFFD700),
                    fontSize = 13.sp
                )
                Text(
                    text = "${index + 1} / ${list.size}",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    val newIdx = if (index <= 0) 0 else index - 1
                    persist(newIdx)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F7A4B))
            ) { Text("السابق", color = Color.White) }

            Button(
                onClick = {
                    val newIdx = if (index >= list.size - 1) index else index + 1
                    persist(newIdx)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) { Text("التالي", color = Color(0xFF062D1A)) }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "سيتم حفظ آخر ذكر وصلت له تلقائياً.\n(يمكننا لاحقاً إضافة مكتبة أذكار كاملة + بحث + عداد تلقائي لكل ذكر)",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}
