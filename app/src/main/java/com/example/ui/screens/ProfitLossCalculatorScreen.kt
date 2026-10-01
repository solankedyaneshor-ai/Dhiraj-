package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TradeSetup
import com.example.ui.theme.TradeAmber
import com.example.ui.theme.TradeCyan
import com.example.ui.theme.TradeGreen
import com.example.ui.theme.TradeRed
import com.example.ui.theme.TradingDarkBg
import com.example.ui.theme.TradingSurface
import com.example.ui.theme.TradingSurfaceElevated
import com.example.ui.theme.TradingSurfaceHighlight
import kotlin.math.abs
import kotlin.math.max

enum class AssetCategory(val label: String, val unitLabel: String) {
    CRYPTO("Crypto", "Units/Coins"),
    FOREX("Forex", "Lots (100k)"),
    STOCKS("Stocks", "Shares")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfitLossCalculatorScreen(
    currentSetup: TradeSetup?,
    modifier: Modifier = Modifier
) {
    var accountBalanceStr by remember { mutableStateOf("10000") }
    var riskPercentStr by remember { mutableStateOf("2.0") }
    var entryPriceStr by remember {
        mutableStateOf(if (currentSetup != null && currentSetup.entryPrice > 0) currentSetup.entryPrice.toString() else "64000")
    }
    var stopLossStr by remember {
        mutableStateOf(if (currentSetup != null && currentSetup.stopLoss > 0) currentSetup.stopLoss.toString() else "62800")
    }
    var tp1Str by remember {
        mutableStateOf(if (currentSetup != null && currentSetup.takeProfit1 > 0) currentSetup.takeProfit1.toString() else "66500")
    }
    var tp2Str by remember {
        mutableStateOf(if (currentSetup != null && currentSetup.takeProfit2 > 0) currentSetup.takeProfit2.toString() else "69000")
    }
    var leverageStr by remember { mutableStateOf("1") }
    var selectedCategory by remember { mutableStateOf(AssetCategory.CRYPTO) }

    // Parse values
    val accountBalance = accountBalanceStr.toDoubleOrNull() ?: 10000.0
    val riskPercent = riskPercentStr.toDoubleOrNull() ?: 2.0
    val entryPrice = entryPriceStr.toDoubleOrNull() ?: 0.0
    val stopLoss = stopLossStr.toDoubleOrNull() ?: 0.0
    val tp1 = tp1Str.toDoubleOrNull() ?: 0.0
    val tp2 = tp2Str.toDoubleOrNull() ?: 0.0
    val leverage = (leverageStr.toDoubleOrNull() ?: 1.0).coerceAtLeast(1.0)

    // Computations
    val cashRiskAmount = accountBalance * (riskPercent / 100.0)
    val priceDiffSl = abs(entryPrice - stopLoss)

    val positionSizeUnits = if (entryPrice > 0 && priceDiffSl > 0) {
        when (selectedCategory) {
            AssetCategory.CRYPTO -> cashRiskAmount / priceDiffSl
            AssetCategory.FOREX -> (cashRiskAmount / priceDiffSl) / 100000.0
            AssetCategory.STOCKS -> (cashRiskAmount / priceDiffSl)
        }
    } else 0.0

    val notionalPositionValue = when (selectedCategory) {
        AssetCategory.CRYPTO -> positionSizeUnits * entryPrice
        AssetCategory.FOREX -> positionSizeUnits * 100000.0 * entryPrice
        AssetCategory.STOCKS -> positionSizeUnits * entryPrice
    }

    val marginRequired = if (leverage > 0) notionalPositionValue / leverage else notionalPositionValue

    val priceDiffTp1 = abs(tp1 - entryPrice)
    val priceDiffTp2 = abs(tp2 - entryPrice)

    val profitTp1 = when (selectedCategory) {
        AssetCategory.CRYPTO -> positionSizeUnits * priceDiffTp1
        AssetCategory.FOREX -> positionSizeUnits * 100000.0 * priceDiffTp1
        AssetCategory.STOCKS -> positionSizeUnits * priceDiffTp1
    }

    val profitTp2 = when (selectedCategory) {
        AssetCategory.CRYPTO -> positionSizeUnits * priceDiffTp2
        AssetCategory.FOREX -> positionSizeUnits * 100000.0 * priceDiffTp2
        AssetCategory.STOCKS -> positionSizeUnits * priceDiffTp2
    }

    val rr1 = if (priceDiffSl > 0) priceDiffTp1 / priceDiffSl else 0.0
    val rr2 = if (priceDiffSl > 0) priceDiffTp2 / priceDiffSl else 0.0

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TradingDarkBg)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("calculator_screen")
    ) {
        // Title Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = "Calculator",
                    tint = TradeCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Risk & P/L Calculator",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Reset Button
            Button(
                onClick = {
                    accountBalanceStr = "10000"
                    riskPercentStr = "2.0"
                    entryPriceStr = "64000"
                    stopLossStr = "62800"
                    tp1Str = "66500"
                    tp2Str = "69000"
                    leverageStr = "1"
                },
                colors = ButtonDefaults.buttonColors(containerColor = TradingSurfaceHighlight),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("calculator_reset_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Reset", color = Color(0xFF94A3B8), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Calculate exact position sizing, cash risk, and profit targets with capital preservation.",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp
        )

        // Import from active setup banner if available
        if (currentSetup != null && currentSetup.entryPrice > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TradeCyan.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                    .border(1.dp, TradeCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .clickable {
                        entryPriceStr = currentSetup.entryPrice.toString()
                        stopLossStr = currentSetup.stopLoss.toString()
                        tp1Str = currentSetup.takeProfit1.toString()
                        tp2Str = currentSetup.takeProfit2.toString()
                    }
                    .padding(12.dp)
                    .testTag("import_setup_button")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "Import",
                        tint = TradeCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Import from ${currentSetup.pairOrTicker} Setup",
                            color = TradeCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Entry: ${currentSetup.entryPrice} • SL: ${currentSetup.stopLoss} • TP1: ${currentSetup.takeProfit1}",
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = "APPLY",
                        color = TradeCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Asset Category Selector Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssetCategory.values().forEach { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSelected) TradeCyan.copy(alpha = 0.2f) else TradingSurfaceElevated,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) TradeCyan else TradingSurfaceHighlight,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedCategory = cat }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat.label,
                        color = if (isSelected) TradeCyan else Color(0xFF94A3B8),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Inputs Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TradingSurfaceElevated, RoundedCornerShape(14.dp))
                .border(1.dp, TradingSurfaceHighlight, RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "ACCOUNT & RISK PROFILE",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Account Balance
                    OutlinedTextField(
                        value = accountBalanceStr,
                        onValueChange = { accountBalanceStr = it },
                        label = { Text("Account Balance ($)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("account_balance_input"),
                        colors = textFieldColors()
                    )

                    // Risk Percent
                    OutlinedTextField(
                        value = riskPercentStr,
                        onValueChange = { riskPercentStr = it },
                        label = { Text("Risk % Per Trade") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("risk_percent_input"),
                        colors = textFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Risk % chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("1.0", "1.5", "2.0", "3.0", "5.0").forEach { pct ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(TradingDarkBg, RoundedCornerShape(6.dp))
                                .border(
                                    0.5.dp,
                                    if (riskPercentStr == pct) TradeCyan else TradingSurfaceHighlight,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { riskPercentStr = pct }
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$pct%",
                                color = if (riskPercentStr == pct) TradeCyan else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = TradingSurfaceHighlight)
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "PRICE TARGETS",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Entry Price
                    OutlinedTextField(
                        value = entryPriceStr,
                        onValueChange = { entryPriceStr = it },
                        label = { Text("Entry Price") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("calc_entry_price_input"),
                        colors = textFieldColors()
                    )

                    // Stop Loss
                    OutlinedTextField(
                        value = stopLossStr,
                        onValueChange = { stopLossStr = it },
                        label = { Text("Stop Loss") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("calc_stop_loss_input"),
                        colors = textFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // TP1
                    OutlinedTextField(
                        value = tp1Str,
                        onValueChange = { tp1Str = it },
                        label = { Text("Take Profit 1") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("calc_tp1_input"),
                        colors = textFieldColors()
                    )

                    // TP2
                    OutlinedTextField(
                        value = tp2Str,
                        onValueChange = { tp2Str = it },
                        label = { Text("Take Profit 2") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("calc_tp2_input"),
                        colors = textFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Leverage
                OutlinedTextField(
                    value = leverageStr,
                    onValueChange = { leverageStr = it },
                    label = { Text("Leverage (e.g. 1x, 5x, 10x)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("calc_leverage_input"),
                    colors = textFieldColors()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TradingSurfaceElevated, RoundedCornerShape(14.dp))
                .border(1.dp, TradingSurfaceHighlight, RoundedCornerShape(14.dp))
                .padding(16.dp)
                .testTag("calculator_results_card")
        ) {
            Column {
                Text(
                    text = "POSITION SIZING & RETURN SUMMARY",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Recommended Position Size
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TradeCyan.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .border(1.dp, TradeCyan, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "RECOMMENDED POSITION SIZE",
                                color = TradeCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Based on $riskPercent% account risk cutoff",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            text = "${formatQuantity(positionSizeUnits)} ${selectedCategory.unitLabel}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Metric Rows
                MetricRow(
                    label = "Total Cash at Risk",
                    value = "-$${String.format("%,.2f", cashRiskAmount)} (${riskPercent}%)",
                    color = TradeRed
                )
                MetricRow(
                    label = "Notional Position Value",
                    value = "$${String.format("%,.2f", notionalPositionValue)}",
                    color = Color.White
                )
                MetricRow(
                    label = "Margin / Capital Required",
                    value = "$${String.format("%,.2f", marginRequired)}",
                    color = TradeCyan
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = TradingSurfaceHighlight)
                Spacer(modifier = Modifier.height(8.dp))

                // Profit TP1
                MetricRow(
                    label = "Potential Profit (TP1)",
                    value = "+$${String.format("%,.2f", profitTp1)} (${String.format("%.2f", (profitTp1 / accountBalance) * 100)}% Gain)",
                    color = TradeGreen
                )
                MetricRow(
                    label = "Risk / Reward (TP1)",
                    value = "1 : ${String.format("%.2f", rr1)}",
                    color = if (rr1 >= 2.0) TradeGreen else if (rr1 >= 1.5) TradeAmber else TradeRed
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Profit TP2
                MetricRow(
                    label = "Potential Profit (TP2)",
                    value = "+$${String.format("%,.2f", profitTp2)} (${String.format("%.2f", (profitTp2 / accountBalance) * 100)}% Gain)",
                    color = Color(0xFF69F0AE)
                )
                MetricRow(
                    label = "Risk / Reward (TP2)",
                    value = "1 : ${String.format("%.2f", rr2)}",
                    color = TradeGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Risk Disclaimer Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TradingDarkBg, RoundedCornerShape(8.dp))
                .border(0.5.dp, TradingSurfaceHighlight, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Risk Disclaimer",
                    tint = TradeAmber,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Risk Management Rule: Never risk more than 1-2% of total capital on a single setup. TradeVision AI does not execute trades or guarantee profits.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF94A3B8),
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = TradeCyan,
    unfocusedBorderColor = TradingSurfaceHighlight,
    focusedLabelColor = TradeCyan,
    unfocusedLabelColor = Color(0xFF94A3B8),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)

private fun formatQuantity(qty: Double): String {
    return if (qty >= 100) {
        String.format("%,.0f", qty)
    } else if (qty >= 1) {
        String.format("%.2f", qty)
    } else {
        String.format("%.4f", qty)
    }
}
