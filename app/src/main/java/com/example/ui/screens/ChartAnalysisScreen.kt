package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.SampleChart
import com.example.data.SampleChartsProvider
import com.example.model.TradeDecision
import com.example.model.TradeSetup
import com.example.ui.components.ConfluenceBreakdownWidget
import com.example.ui.components.PriceLadderWidget
import com.example.ui.components.TradeChartView
import com.example.ui.theme.TradeAmber
import com.example.ui.theme.TradeCyan
import com.example.ui.theme.TradeGreen
import com.example.ui.theme.TradeRed
import com.example.ui.theme.TradingDarkBg
import com.example.ui.theme.TradingSurfaceElevated
import com.example.ui.theme.TradingSurfaceHighlight
import com.example.viewmodel.AnalysisUiState

@Composable
fun ChartAnalysisScreen(
    uiState: AnalysisUiState,
    currentSetup: TradeSetup?,
    selectedImageUri: Uri?,
    selectedBitmap: android.graphics.Bitmap?,
    customNotes: String,
    onNotesChange: (String) -> Unit,
    onImageSelected: (Uri) -> Unit,
    onSelectSample: (SampleChart) -> Unit,
    onAnalyzeClick: () -> Unit,
    onResetClick: () -> Unit,
    onSaveToJournal: () -> Unit,
    onNavigateToCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Zero-permission Photo Picker for Google Play Policy compliance
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onImageSelected(uri)
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TradingDarkBg)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("chart_analysis_screen")
    ) {
        // App Title & Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(TradeGreen.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .border(1.dp, TradeGreen, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = "Logo",
                        tint = TradeGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "TradeVision AI",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "AI Technical & SMC Setup Engine",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }

            // Reset Button
            Button(
                onClick = onResetClick,
                colors = ButtonDefaults.buttonColors(containerColor = TradingSurfaceHighlight),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("reset_chart_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Reset", color = Color(0xFF94A3B8), fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Regulatory & Safety Disclaimer Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TradingSurfaceElevated, RoundedCornerShape(10.dp))
                .border(0.5.dp, TradingSurfaceHighlight, RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Disclaimer",
                    tint = TradeAmber,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Educational & technical analysis only. Does not connect to brokers or execute trades. Past performance does not guarantee future results.",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Upload / Select Chart Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TradingSurfaceElevated, RoundedCornerShape(14.dp))
                .border(1.dp, TradingSurfaceHighlight, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "SELECT OR UPLOAD TRADING CHART",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Upload Button
                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("upload_screenshot_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TradeCyan.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TradeCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Upload",
                        tint = TradeCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedImageUri != null) "Change Chart Screenshot" else "Upload Chart Screenshot",
                        color = TradeCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // If image selected, show thumbnail preview
                if (selectedBitmap != null || selectedImageUri != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, TradingSurfaceHighlight, RoundedCornerShape(8.dp))
                    ) {
                        if (selectedBitmap != null) {
                            Image(
                                bitmap = selectedBitmap.asImageBitmap(),
                                contentDescription = "Uploaded Chart Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Uploaded Chart Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Overlay badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Custom Screenshot Loaded",
                                color = TradeCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Sample Charts
                Text(
                    text = "OR TEST WITH SAMPLE MARKET SCENARIOS:",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SampleChartsProvider.sampleCharts.forEach { sample ->
                        val isSelected = currentSetup?.sampleChartId == sample.id && selectedImageUri == null
                        val pillColor = when (sample.setup.decision) {
                            TradeDecision.BUY_NOW -> TradeGreen
                            TradeDecision.SELL_NOW -> TradeRed
                            TradeDecision.WAIT -> TradeAmber
                            TradeDecision.NO_TRADE -> Color(0xFF94A3B8)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) pillColor.copy(alpha = 0.2f) else TradingDarkBg,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) pillColor else TradingSurfaceHighlight,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onSelectSample(sample) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = sample.pair.split("/")[0],
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = sample.setup.decision.label.replace(" NOW", ""),
                                    color = pillColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Optional Trader Notes
                OutlinedTextField(
                    value = customNotes,
                    onValueChange = onNotesChange,
                    label = { Text("Trader Notes / Specific Timeframe (Optional)") },
                    placeholder = { Text("e.g. 15M chart, looking for liquidity sweep entry...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trader_notes_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TradeCyan,
                        unfocusedBorderColor = TradingSurfaceHighlight,
                        focusedLabelColor = TradeCyan,
                        unfocusedLabelColor = Color(0xFF94A3B8),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Analyze Action Button
                val isLoading = uiState is AnalysisUiState.Loading

                Button(
                    onClick = onAnalyzeClick,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("analyze_chart_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TradeGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = (uiState as AnalysisUiState.Loading).message,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = "Analyze",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ANALYZE CHART CONFLUENCES",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Error message if any
        if (uiState is AnalysisUiState.Error) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TradeRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .border(1.dp, TradeRed, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = TradeRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.errorMessage,
                        color = Color(0xFFFF8A80),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Active Trade Setup Results
        if (currentSetup != null) {
            Spacer(modifier = Modifier.height(16.dp))

            // Candlestick & Target Ladder Chart
            TradeChartView(setup = currentSetup)

            Spacer(modifier = Modifier.height(14.dp))

            // Actionable Decision & Price Ladder Card
            PriceLadderWidget(setup = currentSetup)

            Spacer(modifier = Modifier.height(14.dp))

            // Confluences Breakdown Card
            ConfluenceBreakdownWidget(setup = currentSetup)

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Save to Journal & Open Calculator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onSaveToJournal,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("save_journal_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TradingSurfaceHighlight),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TradingSurfaceHighlight)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint = TradeCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save Setup",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onNavigateToCalculator,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("open_calculator_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TradeCyan.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TradeCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Calculator",
                        tint = TradeCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Calculate P&L",
                        color = TradeCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
