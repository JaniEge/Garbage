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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dk.soerensen.garbagev1.R
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
    val bins by viewModel.bins.collectAsStateWithLifecycle(initialValue = emptyList())

    var selectedBin by remember { mutableStateOf<Bin?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // 🔥 NEW: permission dialog state
    var showPermissionDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.recycling_title),
                navigationType = NavigationType.NONE
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // --- 🎡 KARRUSEL ---
            if (bins.isNotEmpty()) {
                val pagerState = rememberPagerState(pageCount = { bins.size })

                Text(
                    text = stringResource(R.string.select_bin),
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

            // 🔥 FIX: åbner dialog i stedet for direkte geofence
            Button(
                onClick = { showPermissionDialog = true },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(stringResource(R.string.enable_geofencing))
            }

            // --- 📍 STATIONER ---
            Text(
                text = stringResource(R.string.nearest_stations),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = uiState.error ?: stringResource(R.string.error_text))
                        Button(onClick = viewModel::loadRecyclingStations) {
                            Text(stringResource(R.string.retry_button))
                        }
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

        // --- 📝 Bottom Sheet ---
        if (showSheet && selectedBin != null) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState
            ) {
                BinDetailsSheet(
                    bin = selectedBin!!,
                    onTrackRecycling = { updatedBin ->
                        viewModel.updateBin(updatedBin)
                        showSheet = false
                    }
                )
            }
        }

        // 🔥 NEW: Permission dialog
        if (showPermissionDialog) {
            RequestBackgroundLocationPermission(
                onPermissionGranted = {
                    viewModel.enableGeofencing()
                    showPermissionDialog = false
                },
                onDismiss = {
                    showPermissionDialog = false
                }
            )
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

            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
            ) {
                Text(
                    text = "${bin.count}",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = bin.title)
                    Text(text = stringResource(R.string.recycled_count_format, bin.count))
                }
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
            Text(text = station.name)
            Text(text = stringResource(R.string.address_format, station.address))
        }
    }
}