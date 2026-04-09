package dk.soerensen.garbagev1.ui.features.garbage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dk.soerensen.garbagev1.R
import dk.soerensen.garbagev1.domain.GarbageItem
import dk.soerensen.garbagev1.ui.components.AppTopBar
import dk.soerensen.garbagev1.ui.components.NavigationType

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
                    Icon(Icons.Default.Add, contentDescription = "Tilføj")
                }
            }
        ) { paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.items,
                    key = { it.id }
                ) { item ->
                    // Her mapper vi teksten til dine præcise Firebase ID'er
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

/**
 * Matcher teksten fra dine items med dokument-navnene i Firebase.
 * Baseret på dit screenshot: 'bio', 'food_carton', 'cardboard', etc.
 */
private fun binIdForItemBin(binText: String): String {
    val s = binText.trim().lowercase()
    return when {
        // Matche 'bio' dokumentet i Firebase
        s.contains("food") || s.contains("mad") || s.contains("bio") -> "bio"

        // Matche 'food_carton' dokumentet i Firebase
        s.contains("carton") || s.contains("karton") -> "food_carton"

        // Matche 'cardboard' dokumentet i Firebase
        s.contains("cardboard") || s.contains("pap") -> "cardboard"

        // Standard matches
        s.contains("metal") -> "metal"
        s.contains("paper") || s.contains("papir") -> "paper"
        s.contains("glass") || s.contains("glas") -> "glass"
        s.contains("plastic") || s.contains("plast") -> "plastic"
        s.contains("rest") -> "rest"
        s.contains("farligt") -> "farligt"
        s.contains("textile") || s.contains("tekstil") -> "textile"

        else -> s.replace(" ", "_")
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Billed-sektion
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = item.bin,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Fallback hvis billedet mangler helt
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Tekst-sektion
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Skal i: ${item.bin}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onEdit) {
                Text("✏️", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}