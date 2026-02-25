package dk.soerensen.garbagev1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dk.soerensen.garbagev1.ui.components.SnackBarHandler
import dk.soerensen.garbagev1.ui.navigation.MainNavigation
import dk.soerensen.garbagev1.ui.theme.GarbageV1Theme
import javax.inject.Inject
import androidx.compose.foundation.layout.padding

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var snackBarHandler: SnackBarHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GarbageV1Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackBarHandler.snackBarHostState
                        ) { snackBarData ->
                            Snackbar(snackbarData = snackBarData)
                        }
                    }
                ) { paddingValues ->
                    MainNavigation(
                        snackBarHandler = snackBarHandler,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}