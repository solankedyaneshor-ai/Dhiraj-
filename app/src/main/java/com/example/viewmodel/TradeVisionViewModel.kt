package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.SampleChart
import com.example.data.SampleChartsProvider
import com.example.data.TradeVisionDatabase
import com.example.data.toEntity
import com.example.data.toTradeSetup
import com.example.model.TradeSetup
import com.example.service.GeminiChartAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

sealed interface AnalysisUiState {
    object Idle : AnalysisUiState
    data class Loading(val message: String = "Analyzing chart confluences & SMC structure...") : AnalysisUiState
    data class Success(val setup: TradeSetup) : AnalysisUiState
    data class Error(val errorMessage: String) : AnalysisUiState
}

class TradeVisionViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        TradeVisionDatabase::class.java,
        "tradevision_database"
    ).fallbackToDestructiveMigration(dropAllTables = true).build()

    private val dao = db.tradeAnalysisDao()

    private val _uiState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Idle)
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    private val _currentSetup = MutableStateFlow<TradeSetup?>(null)
    val currentSetup: StateFlow<TradeSetup?> = _currentSetup.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    private val _customNotes = MutableStateFlow("")
    val customNotes: StateFlow<String> = _customNotes.asStateFlow()

    private val _savedJournal = MutableStateFlow<List<TradeSetup>>(emptyList())
    val savedJournal: StateFlow<List<TradeSetup>> = _savedJournal.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        // Load initial default sample setup so the app is immediately alive and useful
        val defaultSample = SampleChartsProvider.sampleCharts.first()
        _currentSetup.value = defaultSample.setup
        _uiState.value = AnalysisUiState.Success(defaultSample.setup)

        // Observe Room database
        viewModelScope.launch {
            dao.getAllAnalyses().collectLatest { entities ->
                val list = entities.map { it.toTradeSetup() }
                _savedJournal.value = list

                // Auto-seed sample charts into journal if empty
                if (list.isEmpty()) {
                    withContext(Dispatchers.IO) {
                        SampleChartsProvider.sampleCharts.forEach { sample ->
                            dao.insertAnalysis(sample.setup.toEntity())
                        }
                    }
                }
            }
        }
    }

    fun setCustomNotes(notes: String) {
        _customNotes.value = notes
    }

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                _selectedBitmap.value = bitmap
                inputStream?.close()
            } catch (e: Exception) {
                Log.e("TradeVisionVM", "Failed to load bitmap from uri", e)
            }
        }
    }

    fun selectSampleChart(sample: SampleChart) {
        _selectedImageUri.value = null
        _selectedBitmap.value = null
        _currentSetup.value = sample.setup
        _uiState.value = AnalysisUiState.Success(sample.setup)
    }

    fun analyzeCurrentChart() {
        val bitmap = _selectedBitmap.value
        val setup = _currentSetup.value

        viewModelScope.launch {
            _uiState.value = AnalysisUiState.Loading("Scanning chart candles, indicators & SMC...")

            if (bitmap != null) {
                val result = GeminiChartAnalyzer.analyzeChartImage(bitmap, _customNotes.value)
                result.onSuccess { newSetup ->
                    val finalSetup = newSetup.copy(
                        imageUri = _selectedImageUri.value?.toString()
                    )
                    _currentSetup.value = finalSetup
                    _uiState.value = AnalysisUiState.Success(finalSetup)
                }.onFailure { err ->
                    _uiState.value = AnalysisUiState.Error(err.message ?: "Analysis failed")
                }
            } else if (setup != null) {
                // If a sample chart is currently active, simulate re-analysis or use local engine
                kotlinx.coroutines.delay(1200) // Brief smooth loading state
                _uiState.value = AnalysisUiState.Success(setup)
            } else {
                _uiState.value = AnalysisUiState.Error("Please upload a chart screenshot or select a sample chart.")
            }
        }
    }

    fun resetAnalysis() {
        _selectedImageUri.value = null
        _selectedBitmap.value = null
        _customNotes.value = ""
        val defaultSample = SampleChartsProvider.sampleCharts.first()
        _currentSetup.value = defaultSample.setup
        _uiState.value = AnalysisUiState.Success(defaultSample.setup)
        _snackbarMessage.value = "Chart analysis reset"
    }

    fun saveCurrentToJournal() {
        val setup = _currentSetup.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val id = dao.insertAnalysis(setup.toEntity().copy(id = 0, timestamp = System.currentTimeMillis()))
            _snackbarMessage.value = "Saved ${setup.pairOrTicker} setup to Journal"
        }
    }

    fun deleteFromJournal(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteById(id)
            _snackbarMessage.value = "Removed from Journal"
        }
    }

    fun updateJournalNotes(id: Long, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateNotes(id, notes)
            _snackbarMessage.value = "Notes updated"
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
