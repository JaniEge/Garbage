package dk.soerensen.garbagev1.ui.features.garbage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dk.soerensen.garbagev1.domain.GarbageItem
import dk.soerensen.garbagev1.ui.components.AppTopBar
import dk.soerensen.garbagev1.ui.components.NavigationType
import dk.soerensen.garbagev1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarbageListScreen(
    onNavigate: (GarbageListViewModel.NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GarbageListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { onNavigate(it) }
    }


    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.list_label),
                    navigationType = NavigationType.BACK,
                    onNavigationClick = viewModel::onUpClicked
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = viewModel::onAddClicked,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Icon(Icons.Default.Add, null)
                }
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.items,
                        key = { it.id }
                    ) { item ->
                        val binId = binIdForItemBin(item.bin)
                        val imageUrl = uiState.binImageUrls[binId]

                        GarbageRow(
                            item = item,
                            imageUrl = imageUrl,
                            onEdit = { viewModel.onEditClicked(item) }
                        )
                    }
                }
            }
        }
    }
}

private fun binIdForItemBin(binText: String): String {
    val s = binText.trim().lowercase()
    return when {
        // ✅ Food
        s.contains("food") || s.contains("mad") || s.contains("bio") -> "food"

        // ✅ Metal
        s.contains("metal") -> "metal"

        // ✅ Paper
        s.contains("paper") || s.contains("papir") -> "paper"

        // ✅ Glass
        s.contains("glass") || s.contains("glas") -> "glass"

        // ✅ Plastic
        s.contains("plastic") || s.contains("plast") -> "plastic"

        // behold dine special cases
        s.contains("cardboard") || s.contains("pap") -> "cardboard"
        s.contains("carton") || s.contains("karton") || s.contains("cartons") -> "food_drink_cartons"

        else -> s
    }
}

@Composable
private fun GarbageRow(
    item: GarbageItem,
    imageUrl: String?,
    onEdit: () -> Unit
) {
    Card(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${item.name} should be placed in: ${item.bin}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "${item.bin} label",
                        modifier = Modifier.size(96.dp)
                    )
                } else {
                    Text(
                        text = "(no image for: ${item.bin})",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onEdit) {
                Text("✏️")
            }
        }
    }
}