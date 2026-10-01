package com.example

import com.example.data.SampleChartsProvider
import com.example.model.TradeDecision
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ExampleUnitTest {
    @Test
    fun testSampleChartsSetupIntegrity() {
        val samples = SampleChartsProvider.sampleCharts
        assertTrue(samples.isNotEmpty())

        val btcSample = samples.find { it.id == "btc_breakout" }
        assertNotNull(btcSample)
        assertEquals(TradeDecision.BUY_NOW, btcSample?.setup?.decision)
        assertTrue(btcSample!!.setup.entryPrice > 0)
        assertTrue(btcSample.setup.stopLoss < btcSample.setup.entryPrice) // For long, SL < entry
        assertTrue(btcSample.setup.takeProfit1 > btcSample.setup.entryPrice) // TP1 > entry
        assertTrue(btcSample.setup.takeProfit2 > btcSample.setup.takeProfit1) // TP2 > TP1
        assertTrue(btcSample.setup.riskRewardRatio1 >= 1.5) // Favorable R:R
    }

    @Test
    fun testBearishSetupIntegrity() {
        val samples = SampleChartsProvider.sampleCharts
        val eurSample = samples.find { it.id == "eur_usd_ob" }
        assertNotNull(eurSample)
        assertEquals(TradeDecision.SELL_NOW, eurSample?.setup?.decision)
        assertTrue(eurSample!!.setup.stopLoss > eurSample.setup.entryPrice) // For short, SL > entry
        assertTrue(eurSample.setup.takeProfit1 < eurSample.setup.entryPrice) // TP1 < entry
    }

    @Test
    fun testRiskRewardComputation() {
        val entry = 64000.0
        val sl = 62000.0
        val tp1 = 68000.0
        val tp2 = 72000.0

        val risk = abs(entry - sl) // 2000
        val reward1 = abs(tp1 - entry) // 4000
        val reward2 = abs(tp2 - entry) // 8000

        val rr1 = reward1 / risk
        val rr2 = reward2 / risk

        assertEquals(2.0, rr1, 0.001)
        assertEquals(4.0, rr2, 0.001)
    }
}
