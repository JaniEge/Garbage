package dk.soerensen.garbagev1.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlin.reflect.KClass

private fun NavDestination?.isInRoute(route: KClass<*>): Boolean {
    return generateSequence(this) { it.parent }
        .any { it.hasRoute(route) }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isGarbageSelected = currentDestination.isInRoute(GarbageGraph::class)
    val isRecyclingSelected = currentDestination.isInRoute(RecyclingGraph::class)
    val isSettingsSelected = currentDestination.isInRoute(SettingsGraph::class)

    NavigationBar(
        modifier = Modifier.height(56.dp),
        windowInsets = WindowInsets(0)
    ) {
        NavigationBarItem(
            selected = isGarbageSelected,
            onClick = {
                navController.navigate(GarbageGraph) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            },
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            label = { Text("Garbage") }
        )

        NavigationBarItem(
            selected = isRecyclingSelected,
            onClick = {
                navController.navigate(RecyclingGraph) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            },
            icon = { Icon(Icons.Default.Refresh, contentDescription = null) },
            label = { Text("Recycling") }
        )

        NavigationBarItem(
            selected = isSettingsSelected,
            onClick = {
                navController.navigate(SettingsGraph) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Settings") }
        )
    }
}