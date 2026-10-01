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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.TradingSurfaceElevated
import com.example.ui.theme.TradingSurfaceHighlight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TradeJournalScreen(
    journalList: List<TradeSetup>,
    onSelectSetup: (TradeSetup) -> Unit,
    onDeleteSetup: (Long) -> Unit,
    onUpdateNotes: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<TradeDecision?>(null) }
    var noteDialogSetup by remember { mutableStateOf<TradeSetup?>(null) }
    var currentNoteText by remember { mutableStateOf("") }

    val filteredList = remember(journalList, selectedFilter) {
        if (selectedFilter == null) journalList
        else journalList.filter { it.decision == selectedFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TradingDarkBg)
            .padding(16.dp)
            .testTag("trade_journal_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = "Journal",
                tint = TradeCyan,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Trade Journal",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${journalList.size} Saved Chart Analyses",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterPill(
                label = "ALL (${journalList.size})",
                isSelected = selectedFilter == null,
                color = TradeCyan,
                onClick = { selectedFilter = null }
            )
            FilterPill(
                label = "BUY",
                isSelected = selectedFilter == TradeDecision.BUY_NOW,
                color = TradeGreen,
                onClick = { selectedFilter = TradeDecision.BUY_NOW }
            )
            FilterPill(
                label = "SELL",
                isSelected = selectedFilter == TradeDecision.SELL_NOW,
                color = TradeRed,
                onClick = { selectedFilter = TradeDecision.SELL_NOW }
            )
            FilterPill(
                label = "WAIT",
                isSelected = selectedFilter == TradeDecision.WAIT,
                color = TradeAmber,
                onClick = { selectedFilter = TradeDecision.WAIT }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Empty",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Saved Setups in this category",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Analyze a chart or choose a sample to add to journal",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList, key = { it.id }) { setup ->
                    JournalItemCard(
                        setup = setup,
                        onOpen = { onSelectSetup(setup) },
                        onDelete = { onDeleteSetup(setup.id) },
                        onEditNotes = {
                            noteDialogSetup = setup
                            currentNoteText = setup.notes
                        }
                    )
                }
            }
        }
    }

    // Notes Editor Dialog
    if (noteDialogSetup != null) {
        val editing = noteDialogSetup!!
        AlertDialog(
            onDismissRequest = { noteDialogSetup = null },
            title = {
                Text(
                    text = "Notes: ${editing.pairOrTicker}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Record execution status, psychology, or trade outcome:",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = currentNoteText,
                        onValueChange = { currentNoteText = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateNotes(editing.id, currentNoteText)
                        noteDialogSetup = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TradeCyan)
                ) {
                    Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { noteDialogSetup = null }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = TradingSurfaceElevated
        )
    }
}

@Composable
private fun JournalItemCard(
    setup: TradeSetup,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
    onEditNotes: () -> Unit
) {
    val decisionColor = when (setup.decision) {
        TradeDecision.BUY_NOW -> TradeGreen
        TradeDecision.SELL_NOW -> TradeRed
        TradeDecision.WAIT -> TradeAmber
        TradeDecision.NO_TRADE -> Color(0xFF94A3B8)
    }

    val dateFormatter = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }
    val formattedDate = remember(setup.timestamp) { dateFormatter.format(Date(setup.timestamp)) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TradingSurfaceElevated, RoundedCornerShape(12.dp))
            .border(1.dp, TradingSurfaceHighlight, RoundedCornerShape(12.dp))
            .clickable { onOpen() }
            .padding(14.dp)
            .testTag("journal_card_${setup.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(decisionColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .border(1.dp, decisionColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = setup.decision.label,
                            color = decisionColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = setup.pairOrTicker,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "• ${setup.timeframe}",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = formattedDate,
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Key prices row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Entry: ${formatShortPrice(setup.entryPrice)}",
                    color = TradeCyan,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "SL: ${formatShortPrice(setup.stopLoss)}",
                    color = TradeRed,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "TP1: ${formatShortPrice(setup.takeProfit1)}",
                    color = TradeGreen,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "R:R 1:${setup.riskRewardRatio1}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (setup.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TradingDarkBg, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = setup.notes,
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onEditNotes,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Notes",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = TradeRed.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .background(TradeCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .clickable { onOpen() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "View",
                            tint = TradeCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "View Setup",
                            color = TradeCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterPill(label: String, isSelected: Boolean, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (isSelected) color.copy(alpha = 0.2f) else TradingSurfaceElevated,
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                if (isSelected) color else TradingSurfaceHighlight,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) color else Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

private fun formatShortPrice(price: Double): String {
    return if (price >= 1000) {
        String.format("%,.0f", price)
    } else if (price >= 1) {
        String.format("%.2f", price)
    } else {
        String.format("%.4f", price)
    }
}
