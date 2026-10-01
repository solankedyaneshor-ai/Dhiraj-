package com.example.model

enum class TradeDecision(val label: String, val subtitle: String) {
    BUY_NOW("BUY NOW", "Strong Bullish Confluence - Long Setup Active"),
    SELL_NOW("SELL NOW", "Strong Bearish Confluence - Short Setup Active"),
    WAIT("WAIT", "Pending Retest / Incomplete Trigger"),
    NO_TRADE("NO TRADE", "Poor Risk:Reward / Contradictory Market Signals")
}

data class ConfluenceItem(
    val name: String,
    val bias: String, // "BULLISH", "BEARISH", "NEUTRAL"
    val detail: String,
    val strength: String = "HIGH" // "HIGH", "MEDIUM", "LOW"
)

data class TradeSetup(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val pairOrTicker: String = "Asset",
    val timeframe: String = "Chart",
    val decision: TradeDecision = TradeDecision.WAIT,
    val decisionReason: String = "",
    val confluenceScore: Int = 50, // 0 - 100%
    val entryType: String = "EXACT_PRICE", // "EXACT_PRICE" or "ZONE"
    val entryPrice: Double = 0.0,
    val entryZoneLow: Double? = null,
    val entryZoneHigh: Double? = null,
    val stopLoss: Double = 0.0,
    val takeProfit1: Double = 0.0,
    val takeProfit2: Double = 0.0,
    val riskPercent: Double = 0.0,
    val profitPercent1: Double = 0.0,
    val profitPercent2: Double = 0.0,
    val riskRewardRatio1: Double = 0.0,
    val riskRewardRatio2: Double = 0.0,
    val confluences: List<ConfluenceItem> = emptyList(),
    val indicatorsFound: List<String> = emptyList(),
    val smcConceptsFound: List<String> = emptyList(),
    val marketStructure: String = "",
    val executionPlan: String = "",
    val invalidationCondition: String = "",
    val imageUri: String? = null,
    val sampleChartId: String? = null,
    val notes: String = ""
)
