package dk.soerensen.garbagev1.ui.features

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.soerensen.garbagev1.R
import dk.soerensen.garbagev1.domain.GarbageItem
import dk.soerensen.garbagev1.ui.components.SnackBarHandler

@Composable
fun GarbageSortingScreen(
    modifier: Modifier = Modifier,
    snackBarHandler: SnackBarHandler,
    viewModel: GarbageViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is GarbageViewModel.UiEffect.ShowSnackbar -> {
                    snackBarHandler.postMessage(
                        msgRes = R.string.deleted_message,
                        arguments = arrayOf(effect.message),
                        actionLabelRes = R.string.undo,
                        onActionClick = { viewModel.onUndoDelete() }
                    )
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        if (!state.showList) {

            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChanged,
                label = { Text("Garbage item") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            if (state.result.isNotBlank()) {
                Text(state.result)
                Spacer(Modifier.height(12.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = viewModel::onWhereClicked) {
                    Text("Where")
                }
                Button(onClick = viewModel::onListClicked) {
                    Text("List")
                }
            }
        } else {

            Text(
                text = "Garbage list",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.items) { item ->
                    GarbageRow(
                        item = item,
                        isSelected = state.selectedItem == item,
                        onClick = { viewModel.onItemClicked(item) },
                        onDelete = { viewModel.onDeleteClicked(item) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(onClick = viewModel::onBackClicked) {
                Text("Back")
            }
        }
    }
}

@Composable
private fun GarbageRow(
    item: GarbageItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "${item.name} should be placed in: ${item.bin}",
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}
