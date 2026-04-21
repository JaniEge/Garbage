package dk.soerensen.garbagev1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import dk.soerensen.garbagev1.data.worker.PeriodicRecyclingReminderWorker
import dk.soerensen.garbagev1.domain.Theme
import dk.soerensen.garbagev1.ui.features.settings.SettingsViewModel
import dk.soerensen.garbagev1.ui.navigation.MainNavigation
import dk.soerensen.garbagev1.ui.theme.GarbageV1Theme
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        schedulePeriodicRecyclingReminder()

        setContent {
            val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

            GarbageV1Theme(
                darkTheme = when (uiState.theme) {
                    Theme.DARK -> true
                    Theme.LIGHT -> false
                    Theme.SYSTEM -> isSystemInDarkTheme()
                }
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->
                    MainNavigation(
                        modifier = Modifier.padding(paddingValues),
                        intent = intent
                    )
                }
            }
        }
    }

    private fun schedulePeriodicRecyclingReminder() {
        val workRequest = PeriodicWorkRequestBuilder<PeriodicRecyclingReminderWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "periodic_recycling_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}