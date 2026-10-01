package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.model.ConfluenceItem
import com.example.model.TradeDecision
import com.example.model.TradeSetup
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "trade_analyses")
data class TradeAnalysisEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val pairOrTicker: String,
    val timeframe: String,
    val decision: String,
    val decisionReason: String,
    val confluenceScore: Int,
    val entryType: String,
    val entryPrice: Double,
    val entryZoneLow: Double?,
    val entryZoneHigh: Double?,
    val stopLoss: Double,
    val takeProfit1: Double,
    val takeProfit2: Double,
    val riskPercent: Double,
    val profitPercent1: Double,
    val profitPercent2: Double,
    val riskRewardRatio1: Double,
    val riskRewardRatio2: Double,
    val confluencesJson: String,
    val indicatorsFoundJson: String,
    val smcConceptsFoundJson: String,
    val marketStructure: String,
    val executionPlan: String,
    val invalidationCondition: String,
    val imageUri: String?,
    val sampleChartId: String?,
    val notes: String
)

class TradeConverters {
    @TypeConverter
    fun fromConfluenceList(list: List<ConfluenceItem>): String {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject()
            obj.put("name", item.name)
            obj.put("bias", item.bias)
            obj.put("detail", item.detail)
            obj.put("strength", item.strength)
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toConfluenceList(json: String?): List<ConfluenceItem> {
        if (json.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<ConfluenceItem>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ConfluenceItem(
                        name = obj.optString("name", ""),
                        bias = obj.optString("bias", "NEUTRAL"),
                        detail = obj.optString("detail", ""),
                        strength = obj.optString("strength", "MEDIUM")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }
}

fun TradeAnalysisEntity.toTradeSetup(): TradeSetup {
    val confluencesList = TradeConverters().toConfluenceList(this.confluencesJson)
    val indicatorsList = parseStringList(this.indicatorsFoundJson)
    val smcList = parseStringList(this.smcConceptsFoundJson)
    val decisionEnum = try {
        TradeDecision.valueOf(this.decision)
    } catch (_: Exception) {
        TradeDecision.WAIT
    }

    return TradeSetup(
        id = this.id,
        timestamp = this.timestamp,
        pairOrTicker = this.pairOrTicker,
        timeframe = this.timeframe,
        decision = decisionEnum,
        decisionReason = this.decisionReason,
        confluenceScore = this.confluenceScore,
        entryType = this.entryType,
        entryPrice = this.entryPrice,
        entryZoneLow = this.entryZoneLow,
        entryZoneHigh = this.entryZoneHigh,
        stopLoss = this.stopLoss,
        takeProfit1 = this.takeProfit1,
        takeProfit2 = this.takeProfit2,
        riskPercent = this.riskPercent,
        profitPercent1 = this.profitPercent1,
        profitPercent2 = this.profitPercent2,
        riskRewardRatio1 = this.riskRewardRatio1,
        riskRewardRatio2 = this.riskRewardRatio2,
        confluences = confluencesList,
        indicatorsFound = indicatorsList,
        smcConceptsFound = smcList,
        marketStructure = this.marketStructure,
        executionPlan = this.executionPlan,
        invalidationCondition = this.invalidationCondition,
        imageUri = this.imageUri,
        sampleChartId = this.sampleChartId,
        notes = this.notes
    )
}

fun TradeSetup.toEntity(): TradeAnalysisEntity {
    return TradeAnalysisEntity(
        id = this.id,
        timestamp = this.timestamp,
        pairOrTicker = this.pairOrTicker,
        timeframe = this.timeframe,
        decision = this.decision.name,
        decisionReason = this.decisionReason,
        confluenceScore = this.confluenceScore,
        entryType = this.entryType,
        entryPrice = this.entryPrice,
        entryZoneLow = this.entryZoneLow,
        entryZoneHigh = this.entryZoneHigh,
        stopLoss = this.stopLoss,
        takeProfit1 = this.takeProfit1,
        takeProfit2 = this.takeProfit2,
        riskPercent = this.riskPercent,
        profitPercent1 = this.profitPercent1,
        profitPercent2 = this.profitPercent2,
        riskRewardRatio1 = this.riskRewardRatio1,
        riskRewardRatio2 = this.riskRewardRatio2,
        confluencesJson = TradeConverters().fromConfluenceList(this.confluences),
        indicatorsFoundJson = stringListToJson(this.indicatorsFound),
        smcConceptsFoundJson = stringListToJson(this.smcConceptsFound),
        marketStructure = this.marketStructure,
        executionPlan = this.executionPlan,
        invalidationCondition = this.invalidationCondition,
        imageUri = this.imageUri,
        sampleChartId = this.sampleChartId,
        notes = this.notes
    )
}

private fun parseStringList(json: String?): List<String> {
    if (json.isNullOrEmpty()) return emptyList()
    val list = mutableListOf<String>()
    try {
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            list.add(array.getString(i))
        }
    } catch (_: Exception) {}
    return list
}

private fun stringListToJson(list: List<String>): String {
    val array = JSONArray()
    list.forEach { array.put(it) }
    return array.toString()
}

@Dao
interface TradeAnalysisDao {
    @Query("SELECT * FROM trade_analyses ORDER BY timestamp DESC")
    fun getAllAnalyses(): Flow<List<TradeAnalysisEntity>>

    @Query("SELECT * FROM trade_analyses WHERE id = :id LIMIT 1")
    suspend fun getAnalysisById(id: Long): TradeAnalysisEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(entity: TradeAnalysisEntity): Long

    @Query("UPDATE trade_analyses SET notes = :notes WHERE id = :id")
    suspend fun updateNotes(id: Long, notes: String)

    @Delete
    suspend fun deleteAnalysis(entity: TradeAnalysisEntity)

    @Query("DELETE FROM trade_analyses WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Database(entities = [TradeAnalysisEntity::class], version = 1, exportSchema = false)
abstract class TradeVisionDatabase : RoomDatabase() {
    abstract fun tradeAnalysisDao(): TradeAnalysisDao
}
