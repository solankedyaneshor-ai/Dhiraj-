package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.TradeSetup
import com.example.ui.screens.ChartAnalysisScreen
import com.example.ui.screens.ProfitLossCalculatorScreen
import com.example.ui.screens.TechnicalGuideScreen
import com.example.ui.screens.TradeJournalScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TradeCyan
import com.example.ui.theme.TradeGreen
import com.example.ui.theme.TradingDarkBg
import com.example.ui.theme.TradingSurface
import com.example.ui.theme.TradingSurfaceHighlight
import com.example.viewmodel.TradeVisionViewModel

enum class NavigationTab(val label: String, val icon: ImageVector, val tag: String) {
    ANALYZE("Analyze", Icons.Default.Analytics, "nav_analyze"),
    CALCULATOR("Calculator", Icons.Default.Calculate, "nav_calculator"),
    JOURNAL("Journal", Icons.Default.Book, "nav_journal"),
    GUIDE("Guide", Icons.AutoMirrored.Filled.MenuBook, "nav_guide")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TradeVisionApp()
            }
        }
    }
}

@Composable
fun TradeVisionApp(
    viewModel: TradeVisionViewModel = viewModel()
) {
    var currentTab by remember { mutableStateOf(NavigationTab.ANALYZE) }
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentSetup by viewModel.currentSetup.collectAsStateWithLifecycle()
    val selectedImageUri by viewModel.selectedImageUri.collectAsStateWithLifecycle()
    val selectedBitmap by viewModel.selectedBitmap.collectAsStateWithLifecycle()
    val customNotes by viewModel.customNotes.collectAsStateWithLifecycle()
    val savedJournal by viewModel.savedJournal.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    // Handle back button on secondary screens
    BackHandler(enabled = currentTab != NavigationTab.ANALYZE) {
        currentTab = NavigationTab.ANALYZE
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = TradingSurface,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                NavigationTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = if (isSelected) TradeCyan else Color(0xFF94A3B8)
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                color = if (isSelected) TradeCyan else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = TradingSurfaceHighlight
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentTab) {
            NavigationTab.ANALYZE -> {
                ChartAnalysisScreen(
                    uiState = uiState,
                    currentSetup = currentSetup,
                    selectedImageUri = selectedImageUri,
                    selectedBitmap = selectedBitmap,
                    customNotes = customNotes,
                    onNotesChange = { viewModel.setCustomNotes(it) },
                    onImageSelected = { viewModel.onImageSelected(it) },
                    onSelectSample = { viewModel.selectSampleChart(it) },
                    onAnalyzeClick = { viewModel.analyzeCurrentChart() },
                    onResetClick = { viewModel.resetAnalysis() },
                    onSaveToJournal = { viewModel.saveCurrentToJournal() },
                    onNavigateToCalculator = { currentTab = NavigationTab.CALCULATOR },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            NavigationTab.CALCULATOR -> {
                ProfitLossCalculatorScreen(
                    currentSetup = currentSetup,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            NavigationTab.JOURNAL -> {
                TradeJournalScreen(
                    journalList = savedJournal,
                    onSelectSetup = { selected ->
                        viewModel.selectSampleChart(
                            com.example.data.SampleChart(
                                id = "journal_${selected.id}",
                                title = selected.pairOrTicker,
                                pair = selected.pairOrTicker,
                                timeframe = selected.timeframe,
                                setup = selected,
                                description = selected.decisionReason
                            )
                        )
                        currentTab = NavigationTab.ANALYZE
                    },
                    onDeleteSetup = { viewModel.deleteFromJournal(it) },
                    onUpdateNotes = { id, notes -> viewModel.updateJournalNotes(id, notes) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            NavigationTab.GUIDE -> {
                TechnicalGuideScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
