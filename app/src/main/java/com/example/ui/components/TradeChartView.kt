package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TradeDecision
import com.example.model.TradeSetup
import com.example.ui.theme.TradeAmber
import com.example.ui.theme.TradeCyan
import com.example.ui.theme.TradeGreen
import com.example.ui.theme.TradeRed
import com.example.ui.theme.TradingDarkBg
import com.example.ui.theme.TradingSurfaceHighlight
import kotlin.math.max
import kotlin.math.min

private data class CandleData(
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float
)

@Composable
fun TradeChartView(
    setup: TradeSetup,
    modifier: Modifier = Modifier
) {
    // Generate realistic relative candlestick series based on the trade decision & prices
    val candles = remember(setup) {
        generateCandlesForSetup(setup)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(TradingDarkBg, RoundedCornerShape(12.dp))
            .border(1.dp, TradingSurfaceHighlight, RoundedCornerShape(12.dp))
            .padding(10.dp)
            .testTag("trade_chart_canvas_box")
    ) {
        Column {
            // Header: Pair & Timeframe badge + Decision Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = setup.pairOrTicker,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(TradingSurfaceHighlight, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = setup.timeframe,
                        color = TradeCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                val badgeColor = when (setup.decision) {
                    TradeDecision.BUY_NOW -> TradeGreen
                    TradeDecision.SELL_NOW -> TradeRed
                    TradeDecision.WAIT -> TradeAmber
                    TradeDecision.NO_TRADE -> Color.Gray
                }

                Box(
                    modifier = Modifier
                        .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .border(1.dp, badgeColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = setup.decision.label,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            // Candlestick & Target Ladder Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .testTag("chart_canvas")
            ) {
                val w = size.width
                val h = size.height

                // Background faint grid lines
                val gridRows = 4
                for (i in 1 until gridRows) {
                    val y = h * (i.toFloat() / gridRows)
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                }

                // Range calculations
                val allHighs = candles.map { it.high }.toMutableList()
                val allLows = candles.map { it.low }.toMutableList()

                val minPrice = (allLows.minOrNull() ?: 100f) * 0.985f
                val maxPrice = (allHighs.maxOrNull() ?: 110f) * 1.015f
                val priceRange = max(0.0001f, maxPrice - minPrice)

                fun priceToY(price: Float): Float {
                    val normalized = (price - minPrice) / priceRange
                    return h - (normalized * h)
                }

                // SMC Zone Box Overlay (e.g. Order Block / FVG)
                if (setup.decision == TradeDecision.BUY_NOW || setup.decision == TradeDecision.WAIT) {
                    val zoneTopPrice = (setup.entryZoneHigh?.toFloat() ?: (setup.entryPrice.toFloat() * 1.004f))
                    val zoneBottomPrice = (setup.entryZoneLow?.toFloat() ?: (setup.entryPrice.toFloat() * 0.996f))
                    val yTop = priceToY(zoneTopPrice)
                    val yBottom = priceToY(zoneBottomPrice)

                    drawRect(
                        color = TradeGreen.copy(alpha = 0.12f),
                        topLeft = Offset(w * 0.45f, yTop),
                        size = Size(w * 0.55f, max(12f, yBottom - yTop))
                    )
                    drawRect(
                        color = TradeGreen.copy(alpha = 0.4f),
                        topLeft = Offset(w * 0.45f, yTop),
                        size = Size(w * 0.55f, max(12f, yBottom - yTop)),
                        style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
                    )
                } else if (setup.decision == TradeDecision.SELL_NOW) {
                    val zoneTopPrice = (setup.entryZoneHigh?.toFloat() ?: (setup.entryPrice.toFloat() * 1.004f))
                    val zoneBottomPrice = (setup.entryZoneLow?.toFloat() ?: (setup.entryPrice.toFloat() * 0.996f))
                    val yTop = priceToY(zoneTopPrice)
                    val yBottom = priceToY(zoneBottomPrice)

                    drawRect(
                        color = TradeRed.copy(alpha = 0.12f),
                        topLeft = Offset(w * 0.45f, yTop),
                        size = Size(w * 0.55f, max(12f, yBottom - yTop))
                    )
                    drawRect(
                        color = TradeRed.copy(alpha = 0.4f),
                        topLeft = Offset(w * 0.45f, yTop),
                        size = Size(w * 0.55f, max(12f, yBottom - yTop)),
                        style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
                    )
                }

                // Draw Candlesticks
                val candleCount = candles.size
                val spacing = w / (candleCount + 1)
                val candleWidth = spacing * 0.65f

                candles.forEachIndexed { i, candle ->
                    val cx = (i + 1) * spacing
                    val isBullish = candle.close >= candle.open
                    val color = if (isBullish) Color(0xFF00E676) else Color(0xFFFF334B)

                    val yHigh = priceToY(candle.high)
                    val yLow = priceToY(candle.low)
                    val yOpen = priceToY(candle.open)
                    val yClose = priceToY(candle.close)

                    // Draw Wick
                    drawLine(
                        color = color,
                        start = Offset(cx, yHigh),
                        end = Offset(cx, yLow),
                        strokeWidth = 2f
                    )

                    // Draw Body
                    val bodyTop = min(yOpen, yClose)
                    val bodyHeight = max(3f, kotlin.math.abs(yClose - yOpen))
                    drawRect(
                        color = color,
                        topLeft = Offset(cx - candleWidth / 2f, bodyTop),
                        size = Size(candleWidth, bodyHeight)
                    )
                }

                // Draw Target Price Level Lines (TP2, TP1, Entry, SL)
                val isLong = setup.decision == TradeDecision.BUY_NOW || setup.decision == TradeDecision.WAIT
                val entryY = priceToY(setup.entryPrice.toFloat())
                val slY = priceToY(setup.stopLoss.toFloat())
                val tp1Y = priceToY(setup.takeProfit1.toFloat())
                val tp2Y = priceToY(setup.takeProfit2.toFloat())

                // Stop Loss Line (Red)
                if (setup.stopLoss > 0) {
                    drawLine(
                        color = TradeRed,
                        start = Offset(0f, slY),
                        end = Offset(w, slY),
                        strokeWidth = 2.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
                    )
                }

                // Entry Line (Cyan)
                if (setup.entryPrice > 0) {
                    drawLine(
                        color = TradeCyan,
                        start = Offset(0f, entryY),
                        end = Offset(w, entryY),
                        strokeWidth = 3f
                    )
                }

                // TP1 Line (Green)
                if (setup.takeProfit1 > 0) {
                    drawLine(
                        color = TradeGreen,
                        start = Offset(0f, tp1Y),
                        end = Offset(w, tp1Y),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
                    )
                }

                // TP2 Line (Light Green)
                if (setup.takeProfit2 > 0) {
                    drawLine(
                        color = Color(0xFF69F0AE),
                        start = Offset(0f, tp2Y),
                        end = Offset(w, tp2Y),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
                    )
                }
            }

            // Legend / Level Badges Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LevelPill(label = "SL", price = formatPrice(setup.stopLoss), color = TradeRed)
                Spacer(modifier = Modifier.width(6.dp))
                LevelPill(label = "ENTRY", price = formatPrice(setup.entryPrice), color = TradeCyan)
                Spacer(modifier = Modifier.width(6.dp))
                LevelPill(label = "TP1", price = formatPrice(setup.takeProfit1), color = TradeGreen)
                Spacer(modifier = Modifier.width(6.dp))
                LevelPill(label = "TP2", price = formatPrice(setup.takeProfit2), color = Color(0xFF69F0AE))
            }
        }
    }
}

@Composable
private fun LevelPill(label: String, price: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
            .border(0.5.dp, color.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$label: ",
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = price,
                color = Color.White,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

private fun formatPrice(price: Double): String {
    return if (price >= 1000) {
        String.format("%,.1f", price)
    } else if (price >= 1) {
        String.format("%.2f", price)
    } else {
        String.format("%.4f", price)
    }
}

private fun generateCandlesForSetup(setup: TradeSetup): List<CandleData> {
    val list = mutableListOf<CandleData>()
    val entry = setup.entryPrice.toFloat().let { if (it <= 0) 100f else it }
    val sl = setup.stopLoss.toFloat().let { if (it <= 0) entry * 0.97f else it }
    val tp1 = setup.takeProfit1.toFloat().let { if (it <= 0) entry * 1.05f else it }

    val isBullish = setup.decision == TradeDecision.BUY_NOW || setup.decision == TradeDecision.WAIT

    // Generate 12 sequential realistic candles converging to current setup entry
    var current = if (isBullish) entry * 1.02f else entry * 0.98f

    val steps = if (isBullish) {
        // High, pullback into demand, sweep low, then reversal hammer into entry
        listOf(
            1.02f to 1.01f,
            1.01f to 0.995f,
            0.995f to 0.985f,
            0.985f to 0.980f,
            0.980f to 0.975f, // low sweep near SL
            0.975f to 0.988f,
            0.988f to 0.992f,
            0.992f to 0.989f,
            0.989f to 0.996f,
            0.996f to 0.998f,
            0.998f to 1.000f // current at entry
        )
    } else {
        // Low, rally into supply, sweep high, then rejection into entry
        listOf(
            0.98f to 0.99f,
            0.99f to 1.005f,
            1.005f to 1.015f,
            1.015f to 1.020f,
            1.020f to 1.025f, // high sweep near SL
            1.025f to 1.012f,
            1.012f to 1.008f,
            1.008f to 1.011f,
            1.011f to 1.004f,
            1.004f to 1.002f,
            1.002f to 1.000f // current at entry
        )
    }

    steps.forEach { (openFactor, closeFactor) ->
        val o = entry * openFactor
        val c = entry * closeFactor
        val h = max(o, c) * 1.004f
        val l = min(o, c) * 0.996f
        list.add(CandleData(o, h, l, c))
    }

    return list
}
