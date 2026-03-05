package dk.soerensen.garbagev1.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import dk.soerensen.garbagev1.ui.features.AddWhatScreen
import dk.soerensen.garbagev1.ui.features.AddWhatViewModel
import dk.soerensen.garbagev1.ui.features.AddWhereScreen
import dk.soerensen.garbagev1.ui.features.AddWhereViewModel
import dk.soerensen.garbagev1.ui.features.DetailsScreen
import dk.soerensen.garbagev1.ui.features.DetailsViewModel
import dk.soerensen.garbagev1.ui.features.GarbageListScreen
import dk.soerensen.garbagev1.ui.features.GarbageListViewModel
import dk.soerensen.garbagev1.ui.features.GarbageSortingScreen
import dk.soerensen.garbagev1.ui.features.GarbageSortingViewModel
import dk.soerensen.garbagev1.ui.features.recycling.RecyclingScreen
import dk.soerensen.garbagev1.ui.features.settings.SettingsScreen
import kotlinx.serialization.Serializable

// ---------------------------
// Kotlin Serialization routes
// ---------------------------

@Serializable
object GarbageGraph

@Serializable
object GarbageSortingRoute          // Screen 1: Search (start)

@Serializable
object GarbageListRoute             // Screen 2: List

@Serializable
data class GarbageDetailsRoute(val itemId: String) // Screen 3: Details/Edit

@Serializable
object AddWhatRoute                 // Screen 4: Add flow step 1

@Serializable
data class AddWhereDialogRoute(val what: String)   // Screen 5: Add flow step 2 (dialog)

@Serializable
object RecyclingGraph

@Serializable
object RecyclingRoute

@Serializable
object SettingsGraph

@Serializable
object SettingsRoute

private const val ANIM_MS = 500

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    intent: android.content.Intent? = null
) {
    val navController = rememberNavController()
    val singleTop: NavOptions = navOptions { launchSingleTop = true }

    LaunchedEffect(intent) {
        intent?.let { navController.handleDeepLink(it) }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = GarbageGraph,
            modifier = Modifier.padding(innerPadding)
        ) {

            // -------------------------
            // 1) Garbage (nested graph)
            // -------------------------
            navigation<GarbageGraph>(startDestination = GarbageSortingRoute) {

                composable<GarbageSortingRoute> {
                    GarbageSortingScreen(
                        onNavigate = { event ->
                            when (event) {
                                is GarbageSortingViewModel.NavigationEvent.NavigateToList -> {
                                    navController.navigate(
                                        route = GarbageListRoute,
                                        navOptions = singleTop
                                    )
                                }
                            }
                        }
                    )
                }

                composable<GarbageListRoute>(
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(ANIM_MS)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(ANIM_MS)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(ANIM_MS)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(ANIM_MS)
                        )
                    }
                ) {
                    GarbageListScreen(
                        onNavigate = { event ->
                            when (event) {
                                is GarbageListViewModel.NavigationEvent.NavigateUp -> {
                                    navController.popBackStack()
                                }

                                is GarbageListViewModel.NavigationEvent.NavigateToAdd -> {
                                    navController.navigate(
                                        route = AddWhatRoute,
                                        navOptions = singleTop
                                    )
                                }

                                is GarbageListViewModel.NavigationEvent.NavigateToDetails -> {
                                    navController.navigate(
                                        route = GarbageDetailsRoute(event.itemId),
                                        navOptions = singleTop
                                    )
                                }
                            }
                        }
                    )
                }

                composable<GarbageDetailsRoute>(
                    deepLinks = listOf(
                        navDeepLink { uriPattern = "garbage://items/{itemId}" }
                    ),
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(ANIM_MS)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(ANIM_MS)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(ANIM_MS)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(ANIM_MS)
                        )
                    }
                ) {
                    DetailsScreen(
                        onNavigate = { event ->
                            when (event) {
                                is DetailsViewModel.NavigationEvent.NavigateUp -> {
                                    navController.popBackStack()
                                }
                            }
                        }
                    )
                }
            }

            // -------------------------
            // 2) Recycling (top-level)
            // -------------------------
            navigation<RecyclingGraph>(startDestination = RecyclingRoute) {
                composable<RecyclingRoute> { RecyclingScreen() }
            }

            // -------------------------
            // 3) Settings (top-level)
            // -------------------------
            navigation<SettingsGraph>(startDestination = SettingsRoute) {
                composable<SettingsRoute> { SettingsScreen() }
            }

            // ---------------------------------------
            // 4) Add flow (global / outside the tabs)
            // ---------------------------------------
            composable<AddWhatRoute>(
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(ANIM_MS)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(ANIM_MS)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(ANIM_MS)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(ANIM_MS)
                    )
                }
            ) {
                AddWhatScreen(
                    onNavigate = { event ->
                        when (event) {
                            is AddWhatViewModel.NavigationEvent.NavigateUp -> {
                                navController.popBackStack()
                            }

                            is AddWhatViewModel.NavigationEvent.NavigateToAddWhere -> {
                                navController.navigate(
                                    route = AddWhereDialogRoute(event.what),
                                    navOptions = singleTop
                                )
                            }
                        }
                    }
                )
            }

            dialog<AddWhereDialogRoute> {
                AddWhereScreen(
                    onNavigate = { event ->
                        when (event) {
                            AddWhereViewModel.NavigationEvent.CloseDialog -> {
                                navController.popBackStack()
                            }

                            AddWhereViewModel.NavigationEvent.CloseAndBackToList -> {
                                navController.popBackStack()
                                navController.popBackStack()
                            }
                        }
                    }
                )
            }
        }
    }
}