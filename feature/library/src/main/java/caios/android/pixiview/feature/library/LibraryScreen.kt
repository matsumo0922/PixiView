package caios.android.pixiview.feature.library

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.feature.library.component.LibraryBottomBar
import caios.android.pixiview.feature.library.component.LibraryDestination
import caios.android.pixiview.feature.library.component.LibraryDrawer
import caios.android.pixiview.feature.library.discovery.navigateToLibraryDiscovery
import caios.android.pixiview.feature.library.home.navigateToLibraryHome
import caios.android.pixiview.feature.library.message.navigateToLibraryMessage
import caios.android.pixiview.feature.library.notify.navigateToLibraryNotify
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(
    navigateToPostDetail: (postId: PostId) -> Unit,
    navigateToCreatorTop: (creatorId: CreatorId) -> Unit,
    navigateToCreatorPlans: (creatorId: CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            LibraryDrawer(
                state = drawerState,
                currentDestination = navController.currentBackStackEntryAsState().value?.destination,
                onClickLibrary = navController::navigateToLibrary,
                navigateToSetting = { },
                navigateToAbout = { },
            )
        },
    ) {
        Scaffold(
            modifier = modifier,
            bottomBar = {
                LibraryBottomBar(
                    destinations = LibraryDestination.entries.toImmutableList(),
                    currentDestination = navController.currentBackStackEntryAsState().value?.destination,
                    navigateToDestination = navController::navigateToLibrary,
                )
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) {
            LibraryNavHost(
                modifier = Modifier.padding(it),
                navController = navController,
                openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                },
                navigateToPostDetail = navigateToPostDetail,
                navigateToCreatorTop = navigateToCreatorTop,
                navigateToCreatorPlans = navigateToCreatorPlans,
            )
        }
    }
}

private fun NavHostController.navigateToLibrary(destination: LibraryDestination) {
    val navOption = navOptions {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

    when (destination) {
        LibraryDestination.Home -> navigateToLibraryHome(navOption)
        LibraryDestination.Discovery -> navigateToLibraryDiscovery(navOption)
        LibraryDestination.Notify -> navigateToLibraryNotify(navOption)
        LibraryDestination.Message -> navigateToLibraryMessage(navOption)
    }
}
