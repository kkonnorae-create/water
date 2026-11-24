package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DailyHealthRepository(private val dao: DailyRecordDao) {

  fun observeTodayRecord(dateKey: String): Flow<DailyRecordEntity> {
    return dao.observeRecord(dateKey).map { it ?: DailyRecordEntity(dateKey = dateKey) }
  }

  suspend fun toggleDose(dateKey: String, slot: DoseSlot) {
    val current = dao.getRecord(dateKey) ?: DailyRecordEntity(dateKey = dateKey)
    val updated =
      when (slot) {
        DoseSlot.MORNING -> current.copy(morningTaken = !current.morningTaken)
        DoseSlot.LUNCH -> current.copy(lunchTaken = !current.lunchTaken)
        DoseSlot.DINNER -> current.copy(dinnerTaken = !current.dinnerTaken)
      }.copy(updatedAtMillis = System.currentTimeMillis())
    dao.upsertRecord(updated)
  }

  suspend fun addWaterCup(dateKey: String) {
    val current = dao.getRecord(dateKey) ?: DailyRecordEntity(dateKey = dateKey)
    val updated =
      current.copy(
        waterCups = (current.waterCups + 1).coerceAtMost(99),
        updatedAtMillis = System.currentTimeMillis(),
      )
    dao.upsertRecord(updated)
  }

  suspend fun removeWaterCup(dateKey: String) {
    val current = dao.getRecord(dateKey) ?: DailyRecordEntity(dateKey = dateKey)
    if (current.waterCups <= 0) return
    val updated =
      current.copy(
        waterCups = (current.waterCups - 1).coerceAtLeast(0),
        updatedAtMillis = System.currentTimeMillis(),
      )
    dao.upsertRecord(updated)
  }

  suspend fun clearPreviousDaysRecords(todayDateKey: String) {
    dao.clearPreviousDaysRecords(todayDateKey)
  }
}
