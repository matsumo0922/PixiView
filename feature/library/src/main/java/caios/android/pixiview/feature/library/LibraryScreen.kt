package caios.android.pixiview.feature.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import caios.android.pixiview.feature.library.component.LibraryBottomBar
import caios.android.pixiview.feature.library.component.LibraryDestination
import caios.android.pixiview.feature.library.home.navigateToLibraryHome
import caios.android.pixiview.feature.library.message.navigateToLibraryMessage
import caios.android.pixiview.feature.library.notify.navigateToLibraryNotify
import kotlinx.collections.immutable.toImmutableList

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            LibraryBottomBar(
                destinations = LibraryDestination.entries.toImmutableList(),
                currentDestination = navController.currentBackStackEntryAsState().value?.destination,
                navigateToDestination = {
                    val navOption = navOptions {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }

                    when (it) {
                        LibraryDestination.Home -> navController.navigateToLibraryHome(navOption)
                        LibraryDestination.Notify -> navController.navigateToLibraryNotify(navOption)
                        LibraryDestination.Message -> navController.navigateToLibraryMessage(navOption)
                    }
                },
            )
        }
    ) {
        LibraryNavHost(
            modifier = modifier.padding(it),
            navController = navController,
        )
    }
}
