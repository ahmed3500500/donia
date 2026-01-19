package com.example.islamicapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NamesScreen(modifier: Modifier = Modifier) {
    val names = rememberNames()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF062D1A), Color(0xFF0B5B34))))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "أسماء الله الحسنى",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Bold
        )
        Text(text = "99 اسم مع معنى مختصر", color = Color.White, fontSize = 12.sp)
        Spacer(modifier = Modifier)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(names) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14402A)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "${item.number}. ${item.name}", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                        Text(text = item.meaning, color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

data class AllahName(val number: Int, val name: String, val meaning: String)

private fun rememberNames(): List<AllahName> {
    // قائمة مختصرة (99) — المعاني يمكن تحسينها لاحقاً، لكن هذه تفي بالغرض داخل التطبيق بدون إنترنت.
    val raw = listOf(
        "الله|اسم الجلالة",
        "الرحمن|واسع الرحمة",
        "الرحيم|دائم الرحمة",
        "الملك|المالك لكل شيء",
        "القدوس|المنزه عن النقص",
        "السلام|مانح السلام",
        "المؤمن|مؤمِّن عباده",
        "المهيمن|الرقيب الحافظ",
        "العزيز|الغالب القوي",
        "الجبار|جابر الكسر",
        "المتكبر|العظيم",
        "الخالق|المُبدِع",
        "البارئ|مُوجد الخلق",
        "المصور|مُشكِّل الصور",
        "الغفار|كثير المغفرة",
        "القهار|الغالب",
        "الوهاب|كثير العطاء",
        "الرزاق|الرازق",
        "الفتاح|فاتح الأبواب",
        "العليم|العالِم بكل شيء",
        "القابض|يقبض ويمنع",
        "الباسط|يبسط ويرزق",
        "الخافض|يخفض من يشاء",
        "الرافع|يرفع من يشاء",
        "المعز|يعز من يشاء",
        "المذل|يذل من يشاء",
        "السميع|يسمع كل شيء",
        "البصير|يرى كل شيء",
        "الحكم|الحاكم",
        "العدل|العادل",
        "اللطيف|الرفيق",
        "الخبير|العارف",
        "الحليم|لا يعجل بالعقوبة",
        "العظيم|العظيم الشأن",
        "الغفور|غافر الذنب",
        "الشكور|مجازي الخير",
        "العلي|عالٍ فوق خلقه",
        "الكبير|الكبير",
        "الحفيظ|الحافظ",
        "المقيت|الكافي",
        "الحسيب|المحاسِب",
        "الجليل|الجليل",
        "الكريم|الكريم",
        "الرقيب|المراقب",
        "المجيب|مُجيب الدعاء",
        "الواسع|واسع الفضل",
        "الحكيم|حكيم",
        "الودود|محب لعباده",
        "المجيد|ذو المجد",
        "الباعث|يبعث الخلق",
        "الشهيد|الشاهد",
        "الحق|الحق",
        "الوكيل|الكفيل",
        "القوي|القوي",
        "المتين|شديد القوة",
        "الولي|الناصر",
        "الحميد|المحمود",
        "المحصي|أحصى كل شيء",
        "المبدئ|يبدأ الخلق",
        "المعيد|يعيد الخلق",
        "المحيي|يحيي",
        "المميت|يميت",
        "الحي|الحي",
        "القيوم|القائم بنفسه",
        "الواجد|الغني",
        "الماجد|الرفيع",
        "الواحد|الواحد",
        "الأحد|الفرد",
        "الصمد|المقصود",
        "القادر|القادر",
        "المقتدر|تمام القدرة",
        "المقدم|يقدم من يشاء",
        "المؤخر|يؤخر من يشاء",
        "الأول|بلا بداية",
        "الآخر|بلا نهاية",
        "الظاهر|الظاهر",
        "الباطن|الباطن",
        "الوالي|المتولي",
        "المتعالي|المتعالي",
        "البر|كثير البر",
        "التواب|يقبل التوبة",
        "المنتقم|ينتقم",
        "العفو|يعفو",
        "الرؤوف|رؤوف",
        "مالك الملك|مالك الملك",
        "ذو الجلال والإكرام|صاحب الجلال",
        "المقسط|عادل",
        "الجامع|يجمع",
        "الغني|غني",
        "المغني|يغني",
        "المانع|يمنع",
        "الضار|يضر",
        "النافع|ينفع",
        "النور|نور السماوات",
        "الهادي|يهدي",
        "البديع|مبدع",
        "الباقي|دائم",
        "الوارث|الوارث",
        "الرشيد|المرشد",
        "الصبور|الصابر"
    )
    return raw.mapIndexed { index, line ->
        val parts = line.split("|")
        AllahName(index + 1, parts.getOrElse(0) { "" }, parts.getOrElse(1) { "" })
    }
}
