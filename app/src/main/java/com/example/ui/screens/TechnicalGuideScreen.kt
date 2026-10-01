package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TradeAmber
import com.example.ui.theme.TradeCyan
import com.example.ui.theme.TradeGreen
import com.example.ui.theme.TradePurple
import com.example.ui.theme.TradeRed
import com.example.ui.theme.TradingDarkBg
import com.example.ui.theme.TradingSurfaceElevated
import com.example.ui.theme.TradingSurfaceHighlight

@Composable
fun TechnicalGuideScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TradingDarkBg)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("technical_guide_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = "Guide",
                tint = TradeCyan,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "SMC & Confluence Guide",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Institutional Trading Concepts & Methodology",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Safety & Compliance Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TradeAmber.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                .border(1.dp, TradeAmber.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security",
                    tint = TradeAmber,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Safety & No Auto-Trading Architecture",
                        color = TradeAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "TradeVision AI strictly never connects to any broker API, exchanges, or automated trade execution bots. All analysis and parameters are provided for analytical, educational, and decision-support purposes only. Always exercise strict manual risk management.",
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SMC Concepts
        GuideSectionCard(
            title = "SMART MONEY CONCEPTS (SMC)",
            items = listOf(
                GuideItem(
                    tag = "BOS",
                    name = "Break of Structure",
                    color = TradeCyan,
                    desc = "When price breaks past a significant previous swing high in an uptrend (or swing low in a downtrend) with strong displacement candle closing beyond the level, confirming trend continuation."
                ),
                GuideItem(
                    tag = "CHOCH",
                    name = "Change of Character",
                    color = TradePurple,
                    desc = "The first signal of a potential trend reversal. Occurs when price violates the most recent counter-structural swing point, indicating institutional order flow shifting from bullish to bearish (or vice-versa)."
                ),
                GuideItem(
                    tag = "OB",
                    name = "Order Block (Demand/Supply)",
                    color = TradeGreen,
                    desc = "The last opposing candle prior to an aggressive impulsive breakout that creates an imbalance. Represents institutional accumulation or distribution where unfilled limit orders reside."
                ),
                GuideItem(
                    tag = "FVG",
                    name = "Fair Value Gap (Imbalance)",
                    color = TradeAmber,
                    desc = "A 3-candle price pattern where candle 1's wick and candle 3's wick do not overlap, leaving an imbalance in candle 2. Price often returns to rebalance this inefficiency before continuing."
                ),
                GuideItem(
                    tag = "LIQ",
                    name = "Liquidity Sweeps (BSL/SSL)",
                    color = TradeRed,
                    desc = "Clusters of stop losses resting above equal highs (Buy-Side Liquidity) or below equal lows (Sell-Side Liquidity). Institutional algorithms frequently spike through these levels to engineer counter-trend liquidity."
                )
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Indicator Confluences
        GuideSectionCard(
            title = "INDICATOR CONFLUENCE RULES",
            items = listOf(
                GuideItem(
                    tag = "RSI",
                    name = "Relative Strength Index",
                    color = TradeCyan,
                    desc = "Beyond simple overbought (>70) and oversold (<30), TradeVision AI detects Regular Divergences (momentum failing to make a new high/low with price, signaling reversal) and Hidden Divergences (signaling trend continuation)."
                ),
                GuideItem(
                    tag = "MACD",
                    name = "Moving Average Convergence Divergence",
                    color = TradePurple,
                    desc = "Evaluates momentum shifts via signal line crossovers, zero-line transitions, and expanding/contracting histogram bars aligned with market structure."
                ),
                GuideItem(
                    tag = "P/D",
                    name = "Premium vs Discount Zones",
                    color = TradeAmber,
                    desc = "Using Fibonacci 50% Equilibrium: Never buy in Premium (>50% of range). Only buy in Discount (<50%). Conversely, only initiate short setups in Premium zones for optimal mathematical expectancy."
                )
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Golden Risk Rules
        GuideSectionCard(
            title = "TRADEVISION RISK DISCIPLINE",
            items = listOf(
                GuideItem(
                    tag = "1:2+",
                    name = "Minimum Risk:Reward Ratio",
                    color = TradeGreen,
                    desc = "Never take a trade where potential reward at TP1 is less than 1.5x the risk. Aim for 1:2 to 1:3 setups to remain profitable even with a 40% win rate."
                ),
                GuideItem(
                    tag = "1-2%",
                    name = "Strict Capital Preservation",
                    color = TradeRed,
                    desc = "Limit total risk on any individual trade setup to a maximum of 1% to 2% of your overall account balance, calculated via our Position Sizing Calculator."
                ),
                GuideItem(
                    tag = "BE",
                    name = "Move to Breakeven at TP1",
                    color = TradeCyan,
                    desc = "Once Take Profit 1 is reached, take partial profits (e.g. 50%) and move your Stop Loss to the exact Entry Price, eliminating downside risk for the remainder of the trade."
                )
            )
        )
    }
}

private data class GuideItem(
    val tag: String,
    val name: String,
    val color: Color,
    val desc: String
)

@Composable
private fun GuideSectionCard(title: String, items: List<GuideItem>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TradingSurfaceElevated, RoundedCornerShape(14.dp))
            .border(1.dp, TradingSurfaceHighlight, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            items.forEachIndexed { index, item ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(item.color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(1.dp, item.color.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.tag,
                            color = item.color,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.desc,
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }

                if (index < items.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = TradingSurfaceHighlight.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
