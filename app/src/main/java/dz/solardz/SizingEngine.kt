package dz.solardz

/**
 * Clean-room solar sizing engine for Algeria. Pure Kotlin, no Android deps.
 *
 * Verified defaults (see docs/RESEARCH.md):
 * - GHI north (Alger/Oran/Annaba): ~4.6-5.0 kWh/m2/day; Sahara (Adrar/Tamanrasset): ~6.4-6.6
 * - Default peak-sun-hours used: 5.5 h (editable per wilaya 4.5..6.5)
 * - Performance ratio PR = 0.75; system losses margin 20%
 * - Panel prices 2025: 400W=25,000 DZD, 500W=30,000 DZD; battery unit ~= 50,000 DZD
 */
data class Appliance(val name: String, val watts: Double, val hoursPerDay: Double, val qty: Int = 1) {
    fun dailyWh(): Double = watts * hoursPerDay * qty
}

data class SizingInput(
    val dailyKwh: Double,
    val sunHours: Double = 5.5,
    val autonomyDays: Double = 1.0,
    val batteryVoltage: Double = 24.0,
    val batteryDod: Double = 0.5,
    val performanceRatio: Double = 0.75,
)

data class SizingResult(
    val pvKw: Double,
    val panels500w: Int,
    val batteryKwh: Double,
    val batteryAh: Double,
    val inverterKw: Double,
    val estimatedCostDzd: Long,
)

object SizingEngine {
    const val PANEL_W = 500.0
    const val PANEL_PRICE_DZD = 30_000L
    const val BATTERY_KWH_PRICE_DZD = 50_000L
    const val INVERTER_DZD_PER_KW = 35_000L
    const val BOS_DZD_PER_KW = 40_000L

    val ZONE_SUN_HOURS = mapOf(
        "Littoral (Alger, Oran, Annaba)" to 4.8,
        "Hauts Plateaux (Setif, Djelfa)" to 5.5,
        "Sahara (Adrar, Tamanrasset, Bechar)" to 6.4,
    )

    fun appliancesToDailyKwh(list: List<Appliance>): Double =
        list.sumOf { it.dailyWh() } / 1000.0

    fun size(i: SizingInput): SizingResult {
        require(i.dailyKwh > 0) { "dailyKwh must be > 0" }
        require(i.sunHours in 2.0..9.0) { "sunHours out of range" }
        val pvKw = i.dailyKwh / (i.sunHours * i.performanceRatio)
        val panels = kotlin.math.ceil(pvKw * 1000.0 / PANEL_W).toInt()
        val battKwh = i.dailyKwh * i.autonomyDays / i.batteryDod
        val battAh = battKwh * 1000.0 / i.batteryVoltage
        val inverterKw = kotlin.math.ceil(pvKw * 1.25 * 10.0) / 10.0
        val cost = panels * PANEL_PRICE_DZD +
            (battKwh * BATTERY_KWH_PRICE_DZD / 2.4).toLong() +
            (inverterKw * INVERTER_DZD_PER_KW).toLong() +
            (pvKw * BOS_DZD_PER_KW).toLong()
        return SizingResult(pvKw, panels, battKwh, battAh, inverterKw, cost)
    }
}
