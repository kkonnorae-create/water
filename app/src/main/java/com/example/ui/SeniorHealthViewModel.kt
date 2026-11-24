package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.DailyHealthRepository
import com.example.data.DoseSlot
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class SeniorHealthUiState(
  val dateKey: String = "",
  val dateDisplay: String = "",
  val dayOfWeekDisplay: String = "",
  val morningTaken: Boolean = false,
  val lunchTaken: Boolean = false,
  val dinnerTaken: Boolean = false,
  val waterCups: Int = 0,
  val dailyWaterGoal: Int = 8,
) {
  val takenMedicationCount: Int
    get() = listOf(morningTaken, lunchTaken, dinnerTaken).count { it }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SeniorHealthViewModel(
  private val repository: DailyHealthRepository,
  private val dateProvider: () -> Date = { Calendar.getInstance().time },
  enableMidnightWatcher: Boolean = true,
) : ViewModel() {

  private val currentDateKey = MutableStateFlow(formatDateKey(dateProvider()))

  init {
    viewModelScope.launch { repository.clearPreviousDaysRecords(currentDateKey.value) }
    if (enableMidnightWatcher) {
      viewModelScope.launch {
        while (isActive) {
          val waitMillis = millisUntilNextMidnight(dateProvider())
          delay(waitMillis)
          refreshDateIfChanged()
        }
      }
    }
  }

  val uiState: StateFlow<SeniorHealthUiState> =
    currentDateKey
      .flatMapLatest { dateKey ->
        repository.observeTodayRecord(dateKey).map { record ->
          val displayDate = parseDateKey(record.dateKey) ?: dateProvider()
          SeniorHealthUiState(
            dateKey = record.dateKey,
            dateDisplay = formatDateDisplay(displayDate),
            dayOfWeekDisplay = formatDayOfWeek(displayDate),
            morningTaken = record.morningTaken,
            lunchTaken = record.lunchTaken,
            dinnerTaken = record.dinnerTaken,
            waterCups = record.waterCups,
            dailyWaterGoal = 8,
          )
        }
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue =
          SeniorHealthUiState(
            dateKey = currentDateKey.value,
            dateDisplay = formatDateDisplay(dateProvider()),
            dayOfWeekDisplay = formatDayOfWeek(dateProvider()),
          ),
      )

  fun refreshDateIfChanged() {
    val latestKey = formatDateKey(dateProvider())
    if (latestKey != currentDateKey.value) {
      currentDateKey.value = latestKey
      viewModelScope.launch { repository.clearPreviousDaysRecords(latestKey) }
    }
  }

  fun toggleDose(slot: DoseSlot) {
    val dateKey = currentDateKey.value
    viewModelScope.launch { repository.toggleDose(dateKey, slot) }
  }

  fun addWaterCup() {
    val dateKey = currentDateKey.value
    viewModelScope.launch { repository.addWaterCup(dateKey) }
  }

  fun removeWaterCup() {
    val dateKey = currentDateKey.value
    viewModelScope.launch { repository.removeWaterCup(dateKey) }
  }

  companion object {
    fun formatDateKey(date: Date): String {
      return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
    }

    fun parseDateKey(dateKey: String): Date? {
      return runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateKey) }.getOrNull()
    }

    fun formatDateDisplay(date: Date): String {
      return SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA).format(date)
    }

    fun formatDayOfWeek(date: Date): String {
      return SimpleDateFormat("EEEE", Locale.KOREA).format(date)
    }

    fun millisUntilNextMidnight(now: Date): Long {
      val nextMidnight =
        Calendar.getInstance().apply {
          time = now
          add(Calendar.DAY_OF_YEAR, 1)
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 100)
        }
      return (nextMidnight.timeInMillis - now.time).coerceAtLeast(100L)
    }
  }
}

class SeniorHealthViewModelFactory(private val repository: DailyHealthRepository) :
  ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(SeniorHealthViewModel::class.java)) {
      return SeniorHealthViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
  }
}
