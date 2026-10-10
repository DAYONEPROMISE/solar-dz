package dz.solardz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { SolarDzApp() }
    }
}

enum class UiLang(val tag: String) { FR("fr"), AR("ar") }

@Composable
fun SolarDzApp() {
    val context = LocalContext.current
    var darkTheme by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var lang by rememberSaveable { mutableStateOf(UiLang.FR) }
    val useDark = darkTheme ?: isSystemInDarkTheme()
    val colorScheme = when {
        useDark -> dynamicDarkColorScheme(context)
        else -> dynamicLightColorScheme(context)
    }
    CompositionLocalProvider(
        LocalLayoutDirection provides if (lang == UiLang.AR) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        MaterialTheme(colorScheme = colorScheme) {
            SolarDzHome(lang, { lang = it }, useDark, darkTheme, { darkTheme = it })
        }
    }
}

private fun t(lang: UiLang, fr: String, ar: String) = if (lang == UiLang.FR) fr else ar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarDzHome(
    lang: UiLang,
    onLangChange: (UiLang) -> Unit,
    useDark: Boolean,
    darkOverride: Boolean?,
    onDarkChange: (Boolean?) -> Unit,
) {
    var dailyKwh by rememberSaveable { mutableStateOf("6.0") }
    var zone by rememberSaveable { mutableStateOf("Hauts Plateaux (Setif, Djelfa)") }
    var autonomy by rememberSaveable { mutableStateOf("1.0") }
    var result by remember { mutableStateOf<SizingResult?>(null) }
    var error by remember { mutableStateOf("") }
    val sunH = SizingEngine.ZONE_SUN_HOURS[zone] ?: 5.5
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t(lang, "SolarDZ", "سولار ديزاد")) },
                actions = {
                    IconButton(
                        onClick = { onLangChange(if (lang == UiLang.FR) UiLang.AR else UiLang.FR) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Filled.Language, contentDescription = t(lang, "Langue FR/AR", "اللغة"))
                    }
                    IconButton(
                        onClick = { onDarkChange(if (darkOverride == null) !useDark else null) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            if (useDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = t(lang, "Mode sombre", "الوضع الليلي")
                        )
                    }
                }
            )
        }
    ) { padding ->
        BoxWithConstraints(
            Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding()
        ) {
            val wide = maxWidth >= 600.dp
            val calc: () -> Unit = {
                try {
                    result = SizingEngine.size(SizingInput(dailyKwh.toDouble(), sunH, autonomy.toDouble()))
                    error = ""
                } catch (e: Exception) { error = e.message ?: "Erreur" }
            }
            if (wide) {
                Row(Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InputPane(lang, dailyKwh, { dailyKwh = it }, zone, { zone = it }, sunH, autonomy, { autonomy = it }, error, calc, Modifier.weight(1f).verticalScroll(rememberScrollState()))
                    ResultPane(lang, result, Modifier.weight(1f).verticalScroll(rememberScrollState()))
                }
            } else {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp).navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(t(lang, "100% hors-ligne • DZD • FR/AR", "دون إنترنت • دج • عربي/فرنسي"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    InputPane(lang, dailyKwh, { dailyKwh = it }, zone, { zone = it }, sunH, autonomy, { autonomy = it }, error, calc)
                    ResultPane(lang, result)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputPane(lang: UiLang, dailyKwh: String, onDailyKwh: (String) -> Unit, zone: String, onZone: (String) -> Unit, sunH: Double, autonomy: String, onAutonomy: (String) -> Unit, error: String, onCalculate: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value = dailyKwh, onValueChange = onDailyKwh, label = { Text(t(lang, "Consommation (kWh/jour)", "الاستهلاك (kWh/يوم)")) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, isError = error.isNotEmpty(), supportingText = { if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error) }, modifier = Modifier.fillMaxWidth())
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(value = zone, onValueChange = {}, readOnly = true, label = { Text(t(lang, "Zone wilaya", "منطقة الولاية")) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                SizingEngine.ZONE_SUN_HOURS.forEach { (k, v) ->
                    DropdownMenuItem(text = { Text("$k — ${v}h") }, onClick = { onZone(k); expanded = false }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                }
            }
        }
        Text(t(lang, "Heures soleil: $sunH h/jour", "ساعات الشمس: $sunH سا/يوم"), style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(value = autonomy, onValueChange = onAutonomy, label = { Text(t(lang, "Autonomie (jours)", "الاستقلالية (أيام)")) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.fillMaxWidth())
        Button(onClick = onCalculate, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
            Icon(Icons.Filled.Calculate, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(t(lang, "Calculer", "احسب"))
        }
    }
}

@Composable
private fun ResultPane(lang: UiLang, result: SizingResult?, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(t(lang, "Résultat", "النتيجة"), style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        if (result == null) {
            Text(t(lang, "Saisissez vos valeurs puis touchez Calculer.", "أدخل القيم ثم اضغط احسب."), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ResultRow(Icons.Filled.Bolt, t(lang, "Champ PV", "الألواح"), "${"%.2f".format(result.pvKw)} kWc • ${result.panels500w} x 500W")
                    HorizontalDivider()
                    ResultRow(Icons.Filled.Bolt, t(lang, "Batterie", "البطارية"), "${"%.1f".format(result.batteryKwh)} kWh (${"%.0f".format(result.batteryAh)} Ah @24V)")
                    HorizontalDivider()
                    ResultRow(Icons.Filled.Bolt, t(lang, "Onduleur", "المحول"), "${result.inverterKw} kVA")
                    HorizontalDivider()
                    ResultRow(Icons.Filled.Bolt, t(lang, "Coût estimé", "التكلفة التقديرية"), "${result.estimatedCostDzd} DZD")
                }
            }
        }
        Text(t(lang, "Charges types (exemple):", "أحمال نموذجية (مثال):"), style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
        val demo = listOf(Appliance("LED x6", 9.0, 5.0, 6), Appliance("Frigo", 150.0, 8.0), Appliance("TV", 120.0, 4.0), Appliance("Pompe ferme", 750.0, 3.0))
        LazyColumn(modifier = Modifier.heightIn(max = 220.dp)) {
            items(demo) { a -> Text("${a.name}: ${a.dailyWh().toInt()} Wh/j") }
        }
        Text(t(lang, "Total démo:", "مجموع المثال:") + " ${"%.2f".format(SizingEngine.appliancesToDailyKwh(demo))} kWh/j", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Start)
    }
}

@Composable
private fun ResultRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}
