package dk.soerensen.garbagev1.ui.features.garbage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.soerensen.garbagev1.domain.GarbageItem

@Composable
fun DetailsScreen(
    onNavigate: (DetailsViewModel.NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.navigationEvents.collect {
            onNavigate(it)
        }
    }

    uiState.selectedItem?.let { item ->
        DetailsContent(
            modifier = modifier,
            item = item,
            showDeleteDialog = uiState.showDeleteDialog,
            uiEvents = viewModel.uiEvents
        )
    } ?: run {

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Loading...")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsContent(
    item: GarbageItem,
    showDeleteDialog: Boolean,
    uiEvents: DetailsViewModel.UiEvents,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = uiEvents::onUpClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(text = "Edit item")

            TextButton(onClick = uiEvents::onSaveClick) {
                Text(text = "Save")
            }
        }

        Spacer(Modifier.height(height = 16.dp))

        TextField(
            value = item.name,
            onValueChange = uiEvents::onNameChange,
            label = { Text(text = "Name") }
        )

        Spacer(Modifier.height(height = 16.dp))

        TextField(
            value = item.bin,
            onValueChange = uiEvents::onBinChange,
            label = { Text(text = "Bin") }
        )

        Spacer(Modifier.height(height = 24.dp))

        Button(onClick = uiEvents::onDeleteClick) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )

                Spacer(Modifier.width(width = 8.dp))

                Text(text = "Delete")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = uiEvents::onDismissDeleteClick,
            title = { Text(text = "Delete item") },
            text = { Text(text = "Are you sure you want to delete this item?") },
            confirmButton = {
                TextButton(onClick = uiEvents::onConfirmDeleteClick) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = uiEvents::onDismissDeleteClick) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}