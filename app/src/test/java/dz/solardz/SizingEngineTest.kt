package dz.solardz

import org.junit.Assert.*
import org.junit.Test

class SizingEngineTest {
    @Test fun `appliances sum correctly`() {
        val list = listOf(Appliance("LED", 9.0, 5.0, 6), Appliance("Frigo", 150.0, 8.0))
        assertEquals(1.47, SizingEngine.appliancesToDailyKwh(list), 0.001)
    }

    @Test fun `6kwh 5-5h home gives ~1-45kWc`() {
        val r = SizingEngine.size(SizingInput(6.0, 5.5, 1.0))
        assertEquals(1.45, r.pvKw, 0.05)
        assertEquals(3, r.panels500w)
        assertTrue(r.batteryKwh in 11.0..13.0)
        assertTrue(r.inverterKw >= r.pvKw)
        assertTrue(r.estimatedCostDzd > 200_000)
    }

    @Test fun `sahara needs fewer panels than littoral`() {
        val s = SizingEngine.size(SizingInput(6.0, 6.4, 1.0))
        val l = SizingEngine.size(SizingInput(6.0, 4.8, 1.0))
        assertTrue(s.pvKw < l.pvKw)
        assertTrue(s.panels500w <= l.panels500w)
    }

    @Test fun `invalid input rejected`() {
        try { SizingEngine.size(SizingInput(0.0, 5.5, 1.0)); fail() }
        catch (e: IllegalArgumentException) { }
    }

    @Test fun `farm pump 2-25kwh sizes plausibly`() {
        val r = SizingEngine.size(SizingInput(2.25, 5.5, 1.0))
        assertTrue(r.pvKw in 0.4..0.8)
        assertEquals(2, r.panels500w)
    }
}
