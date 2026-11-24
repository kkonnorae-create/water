package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DoseSlot
import com.example.ui.theme.DinnerIndigo
import com.example.ui.theme.DinnerIndigoSoft
import com.example.ui.theme.HerbGreen
import com.example.ui.theme.HerbGreenDark
import com.example.ui.theme.HerbGreenSoft
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.IvoryBackground
import com.example.ui.theme.IvoryBorder
import com.example.ui.theme.IvoryHeaderSurface
import com.example.ui.theme.IvorySurface
import com.example.ui.theme.LunchOrange
import com.example.ui.theme.LunchOrangeSoft
import com.example.ui.theme.MorningAmber
import com.example.ui.theme.MorningAmberSoft
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterBlueDark
import com.example.ui.theme.WaterCardBg
import com.example.ui.theme.WaterCardBorder

@Composable
fun SeniorHealthScreen(
  uiState: SeniorHealthUiState,
  onToggleDose: (DoseSlot) -> Unit,
  onAddWaterCup: () -> Unit,
  onRemoveWaterCup: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val haptic = LocalHapticFeedback.current
  val scrollState = rememberScrollState()

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = IvoryBackground,
    contentWindowInsets = WindowInsets.safeDrawing,
  ) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier =
          Modifier.fillMaxWidth()
            .widthIn(max = 600.dp)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
      ) {
        // 1. Top Date Banner (Large & Clear)
        DateHeaderBanner(
          dateDisplay = uiState.dateDisplay,
          dayOfWeekDisplay = uiState.dayOfWeekDisplay,
          takenCount = uiState.takenMedicationCount,
        )

        // 2. Medication Section Header + 3 Large Vertical Buttons
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Icon(
              imageVector = Icons.Rounded.Medication,
              contentDescription = null,
              tint = HerbGreenDark,
              modifier = Modifier.size(30.dp),
            )
            Text(
              text = stringResource(R.string.med_section_title),
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold,
              color = InkPrimary,
            )
          }

          MedicationCheckButton(
            title = stringResource(R.string.med_morning),
            icon = Icons.Rounded.WbTwilight,
            accentColor = MorningAmber,
            accentSoftColor = MorningAmberSoft,
            isTaken = uiState.morningTaken,
            testTag = "med_morning_button",
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onToggleDose(DoseSlot.MORNING)
            },
          )

          MedicationCheckButton(
            title = stringResource(R.string.med_lunch),
            icon = Icons.Rounded.LightMode,
            accentColor = LunchOrange,
            accentSoftColor = LunchOrangeSoft,
            isTaken = uiState.lunchTaken,
            testTag = "med_lunch_button",
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onToggleDose(DoseSlot.LUNCH)
            },
          )

          MedicationCheckButton(
            title = stringResource(R.string.med_dinner),
            icon = Icons.Rounded.Bedtime,
            accentColor = DinnerIndigo,
            accentSoftColor = DinnerIndigoSoft,
            isTaken = uiState.dinnerTaken,
            testTag = "med_dinner_button",
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onToggleDose(DoseSlot.DINNER)
            },
          )

          AnimatedVisibility(
            visible = uiState.takenMedicationCount == 3,
            enter = expandVertically(animationSpec = tween(250)) + fadeIn(tween(250)),
            exit = shrinkVertically(animationSpec = tween(200)) + fadeOut(tween(200)),
          ) {
            MedicationCelebrationBanner()
          }
        }

        // 3. Water Intake Section (Bottom)
        WaterIntakeCard(
          waterCups = uiState.waterCups,
          dailyGoal = uiState.dailyWaterGoal,
          onAddWaterCup = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onAddWaterCup()
          },
          onRemoveWaterCup = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onRemoveWaterCup()
          },
        )

        Spacer(modifier = Modifier.height(8.dp))
      }
    }
  }
}

@Composable
private fun DateHeaderBanner(
  dateDisplay: String,
  dayOfWeekDisplay: String,
  takenCount: Int,
) {
  val allDone = takenCount == 3
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(24.dp),
    color = IvoryHeaderSurface,
    border = BorderStroke(1.5.dp, IvoryBorder),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 20.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
        text = dateDisplay,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.Bold,
        color = InkPrimary,
        modifier = Modifier.testTag("today_date_text"),
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = dayOfWeekDisplay,
          fontSize = 23.sp,
          fontWeight = FontWeight.SemiBold,
          color = InkSecondary,
        )

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = if (allDone) HerbGreenSoft else IvorySurface,
          border = BorderStroke(1.5.dp, if (allDone) HerbGreen else IvoryBorder),
        ) {
          Text(
            text = if (allDone) "오늘 약 모두 완료!" else "약 $takenCount / 3 완료",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = if (allDone) HerbGreenDark else InkPrimary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun MedicationCelebrationBanner() {
  val message = stringResource(R.string.med_all_done_celebration)
  Surface(
    modifier =
      Modifier.fillMaxWidth().testTag("med_all_done_message").semantics {
        contentDescription = message
      },
    shape = RoundedCornerShape(24.dp),
    color = HerbGreenSoft,
    border = BorderStroke(2.dp, HerbGreen),
    shadowElevation = 2.dp,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 22.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Box(
        modifier = Modifier.size(58.dp).clip(CircleShape).background(Color.White),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Rounded.Celebration,
          contentDescription = null,
          tint = HerbGreen,
          modifier = Modifier.size(36.dp),
        )
      }
      Text(
        text = message,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Bold,
        color = HerbGreenDark,
        textAlign = TextAlign.Center,
      )
    }
  }
}

@Composable
private fun MedicationCheckButton(
  title: String,
  icon: ImageVector,
  accentColor: Color,
  accentSoftColor: Color,
  isTaken: Boolean,
  testTag: String,
  onClick: () -> Unit,
) {
  val containerColor by
    animateColorAsState(
      targetValue = if (isTaken) HerbGreen else IvorySurface,
      animationSpec = tween(durationMillis = 220),
      label = "medContainerColor",
    )
  val borderColor by
    animateColorAsState(
      targetValue = if (isTaken) HerbGreenDark else IvoryBorder,
      animationSpec = tween(durationMillis = 220),
      label = "medBorderColor",
    )
  val contentColor by
    animateColorAsState(
      targetValue = if (isTaken) Color.White else InkPrimary,
      animationSpec = tween(durationMillis = 220),
      label = "medContentColor",
    )

  val statusText =
    if (isTaken) stringResource(R.string.med_status_taken)
    else stringResource(R.string.med_status_pending)

  Surface(
    onClick = onClick,
    modifier =
      Modifier.fillMaxWidth()
        .heightIn(min = 96.dp)
        .minimumInteractiveComponentSize()
        .testTag(testTag)
        .semantics {
          role = Role.Checkbox
          contentDescription = "$title, $statusText"
        },
    shape = RoundedCornerShape(24.dp),
    color = containerColor,
    border = BorderStroke(2.dp, borderColor),
    shadowElevation = if (isTaken) 4.dp else 1.dp,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Box(
          modifier =
            Modifier.size(54.dp)
              .clip(CircleShape)
              .background(if (isTaken) Color.White.copy(alpha = 0.22f) else accentSoftColor),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isTaken) Color.White else accentColor,
            modifier = Modifier.size(32.dp),
          )
        }

        Text(
          text = title,
          fontSize = 28.sp,
          lineHeight = 34.sp,
          fontWeight = FontWeight.Bold,
          color = contentColor,
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
          imageVector =
            if (isTaken) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
          contentDescription = null,
          tint = if (isTaken) Color.White else InkSecondary,
          modifier = Modifier.size(34.dp),
        )
        Text(
          text = statusText,
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = if (isTaken) Color.White else InkSecondary,
        )
      }
    }
  }
}

@Composable
private fun WaterIntakeCard(
  waterCups: Int,
  dailyGoal: Int,
  onAddWaterCup: () -> Unit,
  onRemoveWaterCup: () -> Unit,
) {
  val goalReached = waterCups >= dailyGoal

  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(26.dp),
    color = WaterCardBg,
    border = BorderStroke(2.dp, WaterCardBorder),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(22.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      // Section Title + Goal Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Icon(
            imageVector = Icons.Rounded.WaterDrop,
            contentDescription = null,
            tint = WaterBlue,
            modifier = Modifier.size(28.dp),
          )
          Text(
            text = stringResource(R.string.water_section_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = InkPrimary,
          )
        }

        Text(
          text =
            if (goalReached) stringResource(R.string.water_goal_done)
            else stringResource(R.string.water_goal_hint),
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = if (goalReached) HerbGreenDark else WaterBlueDark,
        )
      }

      // Cup Visual + Huge Count Number
      Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        WaterCupGraphic(waterCups = waterCups, goalCups = dailyGoal)

        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Text(
            text = "${waterCups}잔",
            fontSize = 44.sp,
            lineHeight = 50.sp,
            fontWeight = FontWeight.Bold,
            color = WaterBlueDark,
            modifier = Modifier.testTag("water_count_text"),
          )

          // 8-cup visual indicator row (4 x 2 grid for spacious readability)
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              for (i in 1..4) {
                WaterMiniCupBadge(filled = i <= waterCups)
              }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              for (i in 5..8) {
                WaterMiniCupBadge(filled = i <= waterCups)
              }
            }
          }
        }
      }

      // Giant Primary Water Action Button
      Button(
        onClick = onAddWaterCup,
        modifier =
          Modifier.fillMaxWidth()
            .heightIn(min = 80.dp)
            .minimumInteractiveComponentSize()
            .testTag("add_water_button"),
        shape = RoundedCornerShape(20.dp),
        colors =
          ButtonDefaults.buttonColors(
            containerColor = WaterBlue,
            contentColor = Color.White,
          ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.LocalDrink,
          contentDescription = null,
          modifier = Modifier.size(30.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = stringResource(R.string.water_add_button),
          fontSize = 23.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
        )
      }

      // Optional large undo button when > 0 cups so mistakes are easy to fix
      if (waterCups > 0) {
        OutlinedButton(
          onClick = onRemoveWaterCup,
          modifier =
            Modifier.fillMaxWidth()
              .heightIn(min = 58.dp)
              .minimumInteractiveComponentSize()
              .testTag("remove_water_button"),
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.5.dp, WaterCardBorder),
          colors =
            ButtonDefaults.outlinedButtonColors(
              containerColor = IvorySurface,
              contentColor = InkSecondary,
            ),
        ) {
          Icon(
            imageVector = Icons.Rounded.Undo,
            contentDescription = null,
            modifier = Modifier.size(26.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(R.string.water_undo_button),
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = InkSecondary,
          )
        }
      }
    }
  }
}

@Composable
private fun WaterMiniCupBadge(filled: Boolean) {
  Box(
    modifier =
      Modifier.size(34.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(if (filled) WaterBlue else Color.White),
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      imageVector = Icons.Rounded.WaterDrop,
      contentDescription = null,
      tint = if (filled) Color.White else WaterCardBorder,
      modifier = Modifier.size(22.dp),
    )
  }
}
