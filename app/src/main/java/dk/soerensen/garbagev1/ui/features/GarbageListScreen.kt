package dk.soerensen.garbagev1.ui.features

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.soerensen.garbagev1.domain.GarbageItem
import dk.soerensen.garbagev1.ui.components.SnackBarHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarbageListScreen(
    snackBarHandler: SnackBarHandler,
    onNavigate: (GarbageListViewModel.NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GarbageListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // (kan blive stående selvom listen ikke sletter længere – men kan også fjernes)
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is GarbageListViewModel.UiEffect.ShowUndo -> {
                    snackBarHandler.postMessage(
                        msgRes = dk.soerensen.garbagev1.R.string.deleted_message,
                        effect.itemName,
                        actionLabelRes = dk.soerensen.garbagev1.R.string.undo,
                        onActionClick = { viewModel.onUndoDelete() }
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { onNavigate(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Garbage list") },
                navigationIcon = {
                    IconButton(onClick = viewModel::onUpClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onAddClicked) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.items) { item ->
                    GarbageRow(
                        item = item,
                        onEdit = { viewModel.onEditClicked(item) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun GarbageRow(
    item: GarbageItem,
    onEdit: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${item.name} → ${item.bin}",
                modifier = Modifier.weight(1f)
            )

            //Icon(
            //    imageVector = Icons.Default.Edit,
            //    contentDescription = "Edit",
            //    tint = MaterialTheme.colorScheme.primary
            //)

            TextButton(onClick = onEdit) {
                Text("✏️")
            }
        }
    }
}