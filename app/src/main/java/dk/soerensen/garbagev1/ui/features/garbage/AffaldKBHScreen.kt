package dk.soerensen.garbagev1.ui.features.garbage

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import dk.soerensen.garbagev1.R
import dk.soerensen.garbagev1.ui.components.AppTopBar
import dk.soerensen.garbagev1.ui.components.NavigationType
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AffaldKBHScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.affald_kbh),
                navigationType = NavigationType.BACK
            )
        }
    ) { paddingValues ->
        AndroidView(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    val url = if (Locale.getDefault().language == "en") {
                        "https://international.kk.dk/live/housing/recycling-in-copenhagen"
                    } else {
                        "https://affald.kk.dk/"
                    }
                    loadUrl(url)
                }
            }
        )
    }
}