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
import android.util.Log
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

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
            FloatingActionButton(
                onClick = { /* nothing here */ },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
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

                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(bin.imageUrl)
                                    .listener(
                                        onError = { _, result -> Log.e("Coil", "Error: ${result.throwable}") }
                                    )
                                    .build(),
                                contentDescription = "${bin.title} label",
                                modifier = Modifier
                                    .size(90.dp)  // kvadratisk - samme bredde og højde
                                    .align(Alignment.CenterHorizontally),
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