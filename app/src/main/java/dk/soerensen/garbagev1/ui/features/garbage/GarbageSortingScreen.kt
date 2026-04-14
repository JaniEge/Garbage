package dk.soerensen.garbagev1.ui.features.garbage

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.soerensen.garbagev1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarbageSortingScreen(
    modifier: Modifier = Modifier,
    onNavigate: (GarbageSortingViewModel.NavigationEvent) -> Unit,
    viewModel: GarbageSortingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { onNavigate(it) }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.list_label)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OutlinedTextField(
                    value = state.query,
                    onValueChange = viewModel::onQueryChanged,
                    label = { Text(stringResource(R.string.garbage_item_label)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (state.result.isNotBlank()) {
                    Text(state.result)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = viewModel::onWhereClicked,
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(40.dp)
                    ) {
                        Text(stringResource(R.string.where_button))
                    }

                    Button(
                        onClick = viewModel::onListClicked,
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(40.dp)
                    ) {
                        Text(stringResource(R.string.list_button))
                    }

                    Button(
                        onClick = viewModel::onAffaldKbhClicked,
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(40.dp)
                    ) {
                        Text(stringResource(R.string.affald_kbh))
                    }
                }
            }
        }
    }
}