package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.DailyHealthRepository
import com.example.data.SeniorHealthDatabase
import com.example.ui.SeniorHealthScreen
import com.example.ui.SeniorHealthViewModel
import com.example.ui.SeniorHealthViewModelFactory
import com.example.ui.theme.IvoryBackground
import com.example.ui.theme.SeniorHealthTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.light(IvoryBackground.toArgb(), IvoryBackground.toArgb()),
      navigationBarStyle = SystemBarStyle.light(IvoryBackground.toArgb(), IvoryBackground.toArgb()),
    )

    val database = SeniorHealthDatabase.getInstance(applicationContext)
    val repository = DailyHealthRepository(database.dailyRecordDao())
    val factory = SeniorHealthViewModelFactory(repository)

    setContent {
      SeniorHealthTheme {
        val viewModel: SeniorHealthViewModel = viewModel(factory = factory)
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val context = LocalContext.current

        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
          viewModel.refreshDateIfChanged()
        }

        DisposableEffect(context, viewModel) {
          val receiver =
            object : BroadcastReceiver() {
              override fun onReceive(ctx: Context?, intent: Intent?) {
                viewModel.refreshDateIfChanged()
              }
            }
          val filter =
            IntentFilter().apply {
              addAction(Intent.ACTION_DATE_CHANGED)
              addAction(Intent.ACTION_TIME_CHANGED)
              addAction(Intent.ACTION_TIMEZONE_CHANGED)
              addAction(Intent.ACTION_SCREEN_ON)
            }
          ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
          )
          onDispose { context.unregisterReceiver(receiver) }
        }

        SeniorHealthScreen(
          uiState = uiState,
          onToggleDose = viewModel::toggleDose,
          onAddWaterCup = viewModel::addWaterCup,
          onRemoveWaterCup = viewModel::removeWaterCup,
        )
      }
    }
  }
}
