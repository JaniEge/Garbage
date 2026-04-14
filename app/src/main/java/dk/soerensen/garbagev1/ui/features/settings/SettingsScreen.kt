package dk.soerensen.garbagev1.ui.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.soerensen.garbagev1.R
import dk.soerensen.garbagev1.domain.Theme
import dk.soerensen.garbagev1.ui.components.ThemedPreviews
import dk.soerensen.garbagev1.ui.theme.GarbageV1Theme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(uiState, viewModel.uiEvents)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    uiState: SettingsViewModel.UiState,
    uiEvents: SettingsViewModel.UiEvents
) {
    var expanded by remember { mutableStateOf(value = false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.theme_label),
                color = MaterialTheme.colorScheme.onBackground
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = uiState.theme.name.lowercase(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Theme.entries.forEach { theme ->
                        DropdownMenuItem(
                            text = { Text(text = theme.name.lowercase()) },
                            onClick = {
                                uiEvents.onSetTheme(theme)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@ThemedPreviews
@Composable
private fun SettingsScreenPreview() {
    GarbageV1Theme {
        SettingsScreen(
            uiState = SettingsViewModel.UiState(theme = Theme.LIGHT),
            uiEvents = object : SettingsViewModel.UiEvents {
                override fun onSetTheme(theme: Theme) {}
            }
        )
    }
}