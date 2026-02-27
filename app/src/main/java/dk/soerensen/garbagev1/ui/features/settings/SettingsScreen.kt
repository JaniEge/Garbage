package dk.soerensen.garbagev1.ui.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dk.soerensen.garbagev1.ui.components.ThemedPreviews
import dk.soerensen.garbagev1.ui.theme.GarbageV1Theme

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Settings",
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@ThemedPreviews
@Composable
private fun SettingsScreenPreview() {
    GarbageV1Theme {
        SettingsScreen()
    }
}