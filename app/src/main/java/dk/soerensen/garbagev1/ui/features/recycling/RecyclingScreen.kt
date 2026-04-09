package dk.soerensen.garbagev1.ui.features.recycling

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dk.soerensen.garbagev1.domain.Bin
import dk.soerensen.garbagev1.domain.RecyclingStation
import dk.soerensen.garbagev1.ui.components.AppTopBar
import dk.soerensen.garbagev1.ui.components.NavigationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecyclingScreen(
    modifier: Modifier = Modifier,
    viewModel: RecyclingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // ✅ Henter dine Bins fra Firebase via ViewModel
    val bins by viewModel.bins.collectAsStateWithLifecycle(initialValue = emptyList())

    // State til at styre dit Detail Sheet
    var selectedBin by remember { mutableStateOf<Bin?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Recycling",
                navigationType = NavigationType.NONE
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- 🎡 KARRUSEL SEKTION (Top) ---
            if (bins.isNotEmpty()) {
                val pagerState = rememberPagerState(pageCount = { bins.size })

                Text(
                    text = "Vælg beholder",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 48.dp),
                    pageSpacing = 16.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) { page ->
                    val bin = bins[page]
                    BinCarouselCard(bin = bin, onClick = {
                        selectedBin = bin
                        showSheet = true
                    })
                }
            }

            // --- 📍 GENBRUGSSTATIONER SEKTION (Bund) ---
            Text(
                text = "Nærmeste stationer",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = uiState.error ?: "Fejl")
                        Button(onClick = viewModel::loadRecyclingStations) { Text("Prøv igen") }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = uiState.stations, key = { it.id }) { station ->
                            RecyclingStationCard(station = station)
                        }
                    }
                }
            }
        }

        // --- 📝 MODAL BOTTOM SHEET (Detaljer & Knap) ---
        if (showSheet && selectedBin != null) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState
            ) {
                BinDetailsSheet(
                    bin = selectedBin!!,
                    onTrackRecycling = { updatedBin ->
                        // ✅ Kalder ViewModel funktionen som gemmer i Firebase
                        viewModel.updateBin(updatedBin)
                        showSheet = false
                    }
                )
            }
        }
    }
}

@Composable
fun BinCarouselCard(bin: Bin, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = bin.imageUrl,
                contentDescription = bin.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Titel overlay på billedet
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ) {
                Text(
                    text = bin.title,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RecyclingStationCard(station: RecyclingStation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = station.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Adresse: ${station.address}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}