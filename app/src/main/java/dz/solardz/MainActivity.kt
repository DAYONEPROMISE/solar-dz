package dz.solardz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SolarDzApp() }
    }
}

@Composable
fun SolarDzApp() {
    MaterialTheme {
        var dailyKwh by remember { mutableStateOf("6.0") }
        var zone by remember { mutableStateOf("Hauts Plateaux (Setif, Djelfa)") }
        var autonomy by remember { mutableStateOf("1.0") }
        var result by remember { mutableStateOf<SizingResult?>(null) }
        var error by remember { mutableStateOf("") }
        val sunH = SizingEngine.ZONE_SUN_HOURS[zone] ?: 5.5

        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("SolarDZ — Dimensionnement kit solaire", style = MaterialTheme.typography.headlineSmall)
            Text("100% hors-ligne • DZD • FR/AR", style = MaterialTheme.typography.bodySmall)
            OutlinedTextField(value = dailyKwh, onValueChange = { dailyKwh = it }, label = { Text("Consommation (kWh/jour)") })
            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }) { Text(zone) }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    SizingEngine.ZONE_SUN_HOURS.forEach { (k, v) ->
                        DropdownMenuItem(text = { Text("$k — ${v}h") }, onClick = { zone = k; expanded = false })
                    }
                }
            }
            Text("Heures soleil: $sunH h/jour")
            OutlinedTextField(value = autonomy, onValueChange = { autonomy = it }, label = { Text("Autonomie (jours)") })
            Button(onClick = {
                try {
                    result = SizingEngine.size(
                        SizingInput(dailyKwh.toDouble(), sunH, autonomy.toDouble())
                    ); error = ""
                } catch (e: Exception) { error = e.message ?: "Erreur" }
            }) { Text("Calculer") }
            if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
            result?.let { r ->
                Card { Column(Modifier.padding(12.dp)) {
                    Text("PV: ${"%.2f".format(r.pvKw)} kWc")
                    Text("Panneaux 500W: ${r.panels500w}")
                    Text("Batterie: ${"%.1f".format(r.batteryKwh)} kWh (${"%.0f".format(r.batteryAh)} Ah @24V)")
                    Text("Onduleur: ${r.inverterKw} kVA")
                    Text("Coût estimé: ${r.estimatedCostDzd} DZD")
                } }
            }
            Text("Charges types (exemple):", style = MaterialTheme.typography.titleSmall)
            val demo = listOf(
                Appliance("LED x6", 9.0, 5.0, 6),
                Appliance("Frigo", 150.0, 8.0),
                Appliance("TV", 120.0, 4.0),
                Appliance("Pompe ferme", 750.0, 3.0),
            )
            LazyColumn { items(demo) { a -> Text("${a.name}: ${a.dailyWh().toInt()} Wh/j") } }
            Text("Total démo: ${"%.2f".format(SizingEngine.appliancesToDailyKwh(demo))} kWh/j")
        }
    }
}
