package dk.soerensen.garbagev1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dagger.hilt.android.AndroidEntryPoint
import dk.soerensen.garbagev1.ui.components.SnackBarHandler
import dk.soerensen.garbagev1.ui.features.GarbageSortingScreen
import dk.soerensen.garbagev1.ui.theme.GarbageV1Theme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var snackBarHandler: SnackBarHandler

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GarbageV1Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(stringResource(R.string.app_name))
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    },
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackBarHandler.snackBarHostState
                        )
                    }
                ) { innerPadding ->
                    GarbageSortingScreen(
                        modifier = Modifier.padding(innerPadding),
                        snackBarHandler = snackBarHandler
                    )
                }
            }
        }
    }
}
