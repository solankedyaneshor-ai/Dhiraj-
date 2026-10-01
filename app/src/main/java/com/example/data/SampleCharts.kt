package com.example.data

import com.example.model.ConfluenceItem
import com.example.model.TradeDecision
import com.example.model.TradeSetup

data class SampleChart(
    val id: String,
    val title: String,
    val pair: String,
    val timeframe: String,
    val setup: TradeSetup,
    val description: String
)

object SampleChartsProvider {
    val sampleCharts: List<SampleChart> = listOf(
        SampleChart(
            id = "btc_breakout",
            title = "BTC/USDT 1H Breakout & Retest",
            pair = "BTC/USDT",
            timeframe = "1H",
            description = "Bullish OB retest, 1H BOS confirmed, RSI divergence from oversold.",
            setup = TradeSetup(
                id = 1,
                timestamp = System.currentTimeMillis() - 3600000,
                pairOrTicker = "BTC/USDT",
                timeframe = "1H",
                decision = TradeDecision.BUY_NOW,
                decisionReason = "High-probability long confluence: Liquidity swept below swing low, 1H Bullish CHOCH and BOS formed, clean retest of unmitigated Demand Order Block with RSI divergence.",
                confluenceScore = 88,
                entryType = "ZONE",
                entryPrice = 64350.0,
                entryZoneLow = 64150.0,
                entryZoneHigh = 64450.0,
                stopLoss = 63120.0,
                takeProfit1 = 67200.0,
                takeProfit2 = 69500.0,
                riskPercent = 1.91,
                profitPercent1 = 4.43,
                profitPercent2 = 8.00,
                riskRewardRatio1 = 2.32,
                riskRewardRatio2 = 4.19,
                confluences = listOf(
                    ConfluenceItem("RSI (14)", "BULLISH", "Bullish divergence at 34 oversold turning up with strong volume", "HIGH"),
                    ConfluenceItem("MACD (12,26,9)", "BULLISH", "Bullish crossover confirmed below zero-line with expanding green histogram", "HIGH"),
                    ConfluenceItem("Bullish Order Block", "BULLISH", "Tested top of unmitigated 4H institutional demand block (64,150 - 64,300)", "HIGH"),
                    ConfluenceItem("Market Structure (BOS)", "BULLISH", "Clean 1H Break of Structure above 64,280 swing high", "HIGH"),
                    ConfluenceItem("Fair Value Gap (FVG)", "BULLISH", "1H Imbalance partially filled and held as support", "MEDIUM"),
                    ConfluenceItem("Liquidity Sweep", "BULLISH", "Asian session lows swept into New York open before aggressive displacement", "HIGH"),
                    ConfluenceItem("Support / Resistance", "BULLISH", "Prior multi-day resistance flipped into fresh support level", "MEDIUM")
                ),
                indicatorsFound = listOf("RSI 14", "MACD", "200 EMA", "Volume Profile"),
                smcConceptsFound = listOf("Bullish Order Block", "Break of Structure (BOS)", "Fair Value Gap (FVG)", "Sell-Side Liquidity Sweep (SSL)"),
                marketStructure = "Bullish continuation structure with higher highs and higher lows after reclaiming range value low.",
                executionPlan = "Enter inside the 64,150 - 64,450 zone on 5M bullish rejection candle. Move Stop Loss to breakeven once TP1 is hit. Trail remainder to TP2.",
                invalidationCondition = "A 1-hour candle close below 63,120 invalidates the demand block and structural bias.",
                sampleChartId = "btc_breakout",
                notes = "Optimal trade entry at 64,350 with target set at previous swing high liquidity."
            )
        ),
        SampleChart(
            id = "eur_usd_ob",
            title = "EUR/USD 15M Bearish Retest",
            pair = "EUR/USD",
            timeframe = "15M",
            description = "London high liquidity sweep into 15M supply block, bearish CHOCH.",
            setup = TradeSetup(
                id = 2,
                timestamp = System.currentTimeMillis() - 7200000,
                pairOrTicker = "EUR/USD",
                timeframe = "15M",
                decision = TradeDecision.SELL_NOW,
                decisionReason = "Strong institutional sell setup: Buy-side liquidity swept above 1.0910, aggressive Bearish CHOCH displacement, currently mitigating 15M Supply Order Block in Deep Premium.",
                confluenceScore = 85,
                entryType = "EXACT_PRICE",
                entryPrice = 1.0885,
                entryZoneLow = 1.0880,
                entryZoneHigh = 1.0895,
                stopLoss = 1.0920,
                takeProfit1 = 1.0820,
                takeProfit2 = 1.0760,
                riskPercent = 0.32,
                profitPercent1 = 0.60,
                profitPercent2 = 1.15,
                riskRewardRatio1 = 1.86,
                riskRewardRatio2 = 3.57,
                confluences = listOf(
                    ConfluenceItem("RSI (14)", "BEARISH", "Overbought reading of 73 followed by bearish hidden divergence", "HIGH"),
                    ConfluenceItem("Bearish Order Block", "BEARISH", "15M institutional distribution block active at 1.0890", "HIGH"),
                    ConfluenceItem("Market Structure (CHOCH)", "BEARISH", "Change of Character confirmed with displacement candle breaking 1.0872", "HIGH"),
                    ConfluenceItem("Premium / Discount", "BEARISH", "Price in Deep Premium (> 70.5% Fibonacci of Asian range)", "HIGH"),
                    ConfluenceItem("Liquidity Sweep", "BEARISH", "Previous day high (PDH) tapped and immediately rejected", "HIGH"),
                    ConfluenceItem("Fair Value Gap (FVG)", "BEARISH", "Bearish FVG between 1.0882 - 1.0892 resisting upward wicks", "MEDIUM")
                ),
                indicatorsFound = listOf("RSI", "Session Volume", "Fibonacci Retracement"),
                smcConceptsFound = listOf("Bearish Order Block", "Change of Character (CHOCH)", "Buy-Side Liquidity Purge", "Premium Pricing"),
                marketStructure = "Bearish internal structure transition following London open false breakout.",
                executionPlan = "Sell market at 1.0885 or limit at 1.0890. Hard stop at 1.0920 above the sweep high. Close 50% position at TP1 (1.0820), hold rest for TP2 (1.0760).",
                invalidationCondition = "15-minute body close above 1.0920 invalidates institutional sell thesis.",
                sampleChartId = "eur_usd_ob",
                notes = "Classic London Session turtle soup sweep into supply zone."
            )
        ),
        SampleChart(
            id = "eth_demand_zone",
            title = "ETH/USDT 4H Pending Retest",
            pair = "ETH/USDT",
            timeframe = "4H",
            description = "Pullback towards 4H demand zone and 200 EMA. Waiting for confirmation trigger.",
            setup = TradeSetup(
                id = 3,
                timestamp = System.currentTimeMillis() - 14400000,
                pairOrTicker = "ETH/USDT",
                timeframe = "4H",
                decision = TradeDecision.WAIT,
                decisionReason = "Price is midway through a corrective pullback. Macro trend is bullish, but entry trigger has not printed yet. Await price reaching the 3,210 - 3,260 Demand Zone before entering.",
                confluenceScore = 62,
                entryType = "ZONE",
                entryPrice = 3240.0,
                entryZoneLow = 3210.0,
                entryZoneHigh = 3260.0,
                stopLoss = 3110.0,
                takeProfit1 = 3550.0,
                takeProfit2 = 3780.0,
                riskPercent = 4.01,
                profitPercent1 = 9.57,
                profitPercent2 = 16.67,
                riskRewardRatio1 = 2.38,
                riskRewardRatio2 = 4.15,
                confluences = listOf(
                    ConfluenceItem("Higher Timeframe Trend", "BULLISH", "Daily & 4H structure remain in strong structural uptrend", "HIGH"),
                    ConfluenceItem("EMA Confluence", "BULLISH", "4H 200 EMA coincides with top of demand zone at 3,250", "HIGH"),
                    ConfluenceItem("RSI (14)", "NEUTRAL", "RSI is mid-band at 46, currently resetting from overbought", "MEDIUM"),
                    ConfluenceItem("Demand Order Block", "BULLISH", "Unmitigated 4H demand block waiting between 3,210 and 3,260", "HIGH"),
                    ConfluenceItem("Trigger Status", "NEUTRAL", "NO ENTRY YET: Price has not tapped demand zone or printed reversal candle", "HIGH")
                ),
                indicatorsFound = listOf("200 EMA", "50 EMA", "RSI"),
                smcConceptsFound = listOf("Unmitigated Demand Zone", "Higher Timeframe Trend", "Discount Zone Target"),
                marketStructure = "Corrective downward retracement inside macro bullish wave.",
                executionPlan = "DO NOT BUY NOW. Set price alert at 3,260. When alert triggers, look for 15M Bullish CHOCH or hammer candle inside the zone before entering.",
                invalidationCondition = "If price bounces aggressively before reaching 3,260, re-evaluate. A 4H close below 3,110 cancels long bias.",
                sampleChartId = "eth_demand_zone",
                notes = "Patience required. Do not chase price mid-air without confirmation."
            )
        ),
        SampleChart(
            id = "nvda_chop",
            title = "NVDA Daily Range Equilibrium",
            pair = "NVDA",
            timeframe = "Daily",
            description = "Consolidation in equilibrium with conflicting indicators and poor R:R.",
            setup = TradeSetup(
                id = 4,
                timestamp = System.currentTimeMillis() - 28800000,
                pairOrTicker = "NVDA",
                timeframe = "Daily",
                decision = TradeDecision.NO_TRADE,
                decisionReason = "Unfavorable risk-to-reward. Stock is tightly coiling in the middle of a multi-week consolidation range with no displacement. Both buyers and sellers are trapped. Capital preservation is priority.",
                confluenceScore = 32,
                entryType = "EXACT_PRICE",
                entryPrice = 118.50,
                entryZoneLow = null,
                entryZoneHigh = null,
                stopLoss = 115.00,
                takeProfit1 = 121.50,
                takeProfit2 = 123.00,
                riskPercent = 2.95,
                profitPercent1 = 2.53,
                profitPercent2 = 3.80,
                riskRewardRatio1 = 0.86,
                riskRewardRatio2 = 1.29,
                confluences = listOf(
                    ConfluenceItem("RSI (14)", "NEUTRAL", "Flatline at 50.4 with no divergence or momentum", "LOW"),
                    ConfluenceItem("MACD", "NEUTRAL", "Histogram bars virtually zero, MACD and signal lines entangled", "LOW"),
                    ConfluenceItem("Market Structure", "NEUTRAL", "Inside bar daily cluster, equal highs and equal lows intact", "HIGH"),
                    ConfluenceItem("Risk / Reward", "BEARISH", "Risk/Reward ratio is 0.86 (under minimum 1:1.5 threshold)", "HIGH"),
                    ConfluenceItem("Volume", "BEARISH", "Declining volume during contraction indicating lack of institutional participation", "MEDIUM")
                ),
                indicatorsFound = listOf("RSI", "MACD", "Bollinger Bands", "Volume"),
                smcConceptsFound = listOf("Range Equilibrium (50%)", "Equal Highs & Lows (Inducement)"),
                marketStructure = "Sideways compression / accumulation or distribution phase without confirmed breakout.",
                executionPlan = "PASS. Stand aside. Do not initiate trades in low-probability chop. Wait for a definitive daily candle close outside 115 - 124 range.",
                invalidationCondition = "A high volume breakout above 124.50 or breakdown below 114.00 will initiate a new directional setup.",
                sampleChartId = "nvda_chop",
                notes = "Disciplined traders do not trade low-confluence equilibrium."
            )
        )
    )
}
