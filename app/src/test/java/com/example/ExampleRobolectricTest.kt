package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.DailyHealthRepository
import com.example.data.DoseSlot
import com.example.data.SeniorHealthDatabase
import com.example.ui.SeniorHealthScreen
import com.example.ui.SeniorHealthUiState
import com.example.ui.SeniorHealthViewModel
import com.example.ui.theme.SeniorHealthTheme
import java.util.Calendar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var database: SeniorHealthDatabase
  private lateinit var repository: DailyHealthRepository

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database =
      Room.inMemoryDatabaseBuilder(context, SeniorHealthDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    repository = DailyHealthRepository(database.dailyRecordDao())
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `app name matches 오늘건강`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("오늘건강", appName)
  }

  @Test
  fun `daily medication and water records persist in Room`() = runBlocking {
    val dateKey = "2026-11-17"

    repository.toggleDose(dateKey, DoseSlot.MORNING)
    repository.toggleDose(dateKey, DoseSlot.DINNER)
    repository.addWaterCup(dateKey)
    repository.addWaterCup(dateKey)
    repository.addWaterCup(dateKey)

    // Re-create repository to verify persisted state from Room
    val reloadedRepository = DailyHealthRepository(database.dailyRecordDao())
    val saved = reloadedRepository.observeTodayRecord(dateKey).first()

    assertTrue(saved.morningTaken)
    assertFalse(saved.lunchTaken)
    assertTrue(saved.dinnerTaken)
    assertEquals(3, saved.waterCups)
  }

  @Test
  fun `screen displays date, medication buttons, and water counter`() {
    var toggledSlot: DoseSlot? = null
    var waterAdded = false

    composeTestRule.setContent {
      SeniorHealthTheme {
        SeniorHealthScreen(
          uiState =
            SeniorHealthUiState(
              dateKey = "2026-11-17",
              dateDisplay = "2026년 11월 17일",
              dayOfWeekDisplay = "화요일",
              morningTaken = true,
              lunchTaken = false,
              dinnerTaken = false,
              waterCups = 2,
            ),
          onToggleDose = { toggledSlot = it },
          onAddWaterCup = { waterAdded = true },
          onRemoveWaterCup = {},
        )
      }
    }

    composeTestRule.onNodeWithTag("today_date_text").assertTextContains("2026년 11월 17일")
    composeTestRule.onNodeWithText("아침 약").assertIsDisplayed()
    composeTestRule.onNodeWithText("점심 약").assertIsDisplayed()
    composeTestRule.onNodeWithText("저녁 약").assertIsDisplayed()
    composeTestRule.onNodeWithText("복용 완료").assertIsDisplayed()
    composeTestRule.onNodeWithTag("med_lunch_button").performScrollTo().performClick()
    assertEquals(DoseSlot.LUNCH, toggledSlot)

    composeTestRule.onNodeWithTag("water_count_text").performScrollTo().assertTextContains("2잔")
    composeTestRule.onNodeWithTag("add_water_button").performScrollTo().performClick()
    assertTrue(waterAdded)
  }

  @Test
  fun `midnight rollover clears yesterday record and resets checks and water`() = runBlocking {
    val calendar =
      Calendar.getInstance().apply {
        set(2026, Calendar.NOVEMBER, 17, 23, 59, 30)
        set(Calendar.MILLISECOND, 0)
      }
    var fakeNow = calendar.time

    // Wait delay until next midnight should be ~30.1 seconds
    val waitMillis = SeniorHealthViewModel.millisUntilNextMidnight(fakeNow)
    assertEquals(30_100L, waitMillis)

    val viewModel =
      SeniorHealthViewModel(
        repository = repository,
        dateProvider = { fakeNow },
        enableMidnightWatcher = false,
      )

    // Yesterday's checks and water cups
    val yesterdayKey = "2026-11-17"
    repository.toggleDose(yesterdayKey, DoseSlot.MORNING)
    repository.toggleDose(yesterdayKey, DoseSlot.LUNCH)
    repository.addWaterCup(yesterdayKey)
    repository.addWaterCup(yesterdayKey)

    val beforeMidnight = repository.observeTodayRecord(yesterdayKey).first()
    assertTrue(beforeMidnight.morningTaken)
    assertEquals(2, beforeMidnight.waterCups)

    // Cross midnight to 2026-11-18 00:00:01
    calendar.set(2026, Calendar.NOVEMBER, 18, 0, 0, 1)
    fakeNow = calendar.time

    viewModel.refreshDateIfChanged()

    // Wait briefly for coroutine on Dispatchers.Main/Room to finish
    val todayRecord = viewModel.uiState.first { it.dateKey == "2026-11-18" }
    assertEquals("2026년 11월 18일", todayRecord.dateDisplay)
    assertFalse(todayRecord.morningTaken)
    assertFalse(todayRecord.lunchTaken)
    assertFalse(todayRecord.dinnerTaken)
    assertEquals(0, todayRecord.waterCups)

    // Allow IO/coroutine cleanup to finish and verify yesterday's row was purged
    kotlinx.coroutines.delay(150)
    assertNull(database.dailyRecordDao().getRecord(yesterdayKey))
  }

  @Test
  fun `celebration message appears only when all three medications are taken`() {
    composeTestRule.setContent {
      SeniorHealthTheme {
        SeniorHealthScreen(
          uiState =
            SeniorHealthUiState(
              dateKey = "2026-11-17",
              dateDisplay = "2026년 11월 17일",
              dayOfWeekDisplay = "화요일",
              morningTaken = true,
              lunchTaken = true,
              dinnerTaken = true,
              waterCups = 1,
            ),
          onToggleDose = {},
          onAddWaterCup = {},
          onRemoveWaterCup = {},
        )
      }
    }

    composeTestRule
      .onNodeWithTag("med_all_done_message")
      .performScrollTo()
      .assertIsDisplayed()
    composeTestRule.onNodeWithText("오늘도 건강 챙기기 성공!").assertIsDisplayed()
  }
}
