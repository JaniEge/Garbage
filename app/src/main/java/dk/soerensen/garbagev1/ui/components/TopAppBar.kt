package dk.soerensen.garbagev1.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationType {
    BACK,
    CLOSE,
    NONE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    navigationType: NavigationType = NavigationType.NONE,
    onNavigationClick: (() -> Unit)? = null,
    actionIcon: ImageVector? = null,
    onActionClick: (() -> Unit)? = null,
    actionContentDescription: String? = null
) {
    val navIcon: ImageVector? = when (navigationType) {
        NavigationType.BACK -> Icons.AutoMirrored.Filled.ArrowBack
        NavigationType.CLOSE -> Icons.Filled.Close
        NavigationType.NONE -> null
    }

    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (navIcon != null && onNavigationClick != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(imageVector = navIcon, contentDescription = null)
                }
            }
        },
        actions = {
            if (actionIcon != null && onActionClick != null) {
                IconButton(onClick = onActionClick) {
                    Icon(imageVector = actionIcon, contentDescription = actionContentDescription)
                }
            }
        },

        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}