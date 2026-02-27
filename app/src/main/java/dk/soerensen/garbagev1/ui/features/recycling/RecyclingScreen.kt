package dk.soerensen.garbagev1.ui.features.recycling

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import dk.soerensen.garbagev1.domain.Bin
import dk.soerensen.garbagev1.ui.components.AppTopBar
import dk.soerensen.garbagev1.ui.components.NavigationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecyclingScreen(
    modifier: Modifier = Modifier,
    viewModel: RecyclingViewModel = hiltViewModel()
) {
    val bins by viewModel.bins.collectAsState(initial = emptyList())
    var selectedBin by remember { mutableStateOf<Bin?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Recycling",
                navigationType = NavigationType.NONE
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* nothing here */ }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Choose a bin type", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = bins,
                    key = { it.id }
                ) { bin ->
                    Card(
                        modifier = Modifier
                            .width(240.dp)
                            .height(190.dp)
                            .clickable { selectedBin = bin },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(bin.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))

                            // ✅ FORCE size so it can't measure to 0
                            AsyncImage(
                                model = bin.imageUrl,
                                contentDescription = "${bin.title} label",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(Modifier.height(8.dp))
                            Text(bin.description, maxLines = 2, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }

    selectedBin?.let { bin ->
        ModalBottomSheet(
            onDismissRequest = { selectedBin = null }
        ) {
            BinDetailsSheet(bin = bin)
        }
    }
}