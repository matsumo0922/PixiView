package caios.android.fanbox.feature.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.model.fanbox.id.PostId
import caios.android.fanbox.core.ui.AsyncLoadContents
import caios.android.fanbox.core.ui.extensition.LocalNavigationType
import caios.android.fanbox.core.ui.extensition.PixiViewNavigationType
import caios.android.fanbox.feature.library.component.LibraryBottomBar
import caios.android.fanbox.feature.library.component.LibraryDestination
import caios.android.fanbox.feature.library.component.LibraryDrawer
import caios.android.fanbox.feature.library.component.LibraryNavigationRail
import caios.android.fanbox.feature.library.discovery.navigateToLibraryDiscovery
import caios.android.fanbox.feature.library.home.navigateToLibraryHome
import caios.android.fanbox.feature.library.message.navigateToLibraryMessage
import caios.android.fanbox.feature.library.notify.navigateToLibraryNotify
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(
    navigateToPostSearch: () -> Unit,
    navigateToPostDetailFromHome: (postId: PostId) -> Unit,
    navigateToPostDetailFromSupported: (postId: PostId) -> Unit,
    navigateToCreatorPosts: (creatorId: CreatorId) -> Unit,
    navigateToCreatorPlans: (creatorId: CreatorId) -> Unit,
    navigateToBookmarkedPosts: () -> Unit,
    navigateToFollowerCreators: () -> Unit,
    navigateToSupportingCreators: () -> Unit,
    navigateToPayments: () -> Unit,
    navigateToSettingTop: () -> Unit,
    navigateToAbout: () -> Unit,
    navigateToBillingPlus: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navigationType = LocalNavigationType.current
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                LibraryDrawer(
                    state = drawerState,
                    userData = it.userData,
                    currentDestination = navController.currentBackStackEntryAsState().value?.destination,
                    onClickLibrary = navController::navigateToLibraryDestination,
                    navigateToBookmarkedPosts = navigateToBookmarkedPosts,
                    navigateToFollowingCreators = navigateToFollowerCreators,
                    navigateToSupportingCreators = navigateToSupportingCreators,
                    navigateToPayments = navigateToPayments,
                    navigateToSetting = navigateToSettingTop,
                    navigateToAbout = navigateToAbout,
                    navigateToBillingPlus = navigateToBillingPlus,
                )
            },
        ) {
            Row(Modifier.fillMaxSize()) {
                AnimatedVisibility(navigationType.type == PixiViewNavigationType.NavigationRail) {
                    LibraryNavigationRail(
                        modifier = Modifier.fillMaxHeight(),
                        destinations = LibraryDestination.entries.toImmutableList(),
                        currentDestination = navController.currentBackStackEntryAsState().value?.destination,
                        navigateToDestination = navController::navigateToLibraryDestination,
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                ) {
                    LibraryNavHost(
                        modifier = Modifier.weight(1f),
                        navController = navController,
                        openDrawer = {
                            scope.launch {
                                drawerState.open()
                            }
                        },
                        navigateToPostSearch = navigateToPostSearch,
                        navigateToPostDetailFromHome = navigateToPostDetailFromHome,
                        navigateToPostDetailFromSupported = navigateToPostDetailFromSupported,
                        navigateToCreatorPosts = navigateToCreatorPosts,
                        navigateToCreatorPlans = navigateToCreatorPlans,
                    )

                    AnimatedVisibility(navigationType.type == PixiViewNavigationType.BottomNavigation) {
                        LibraryBottomBar(
                            modifier = Modifier.fillMaxWidth(),
                            destinations = LibraryDestination.entries.toImmutableList(),
                            currentDestination = navController.currentBackStackEntryAsState().value?.destination,
                            navigateToDestination = navController::navigateToLibraryDestination,
                        )
                    }
                }
            }
        }
    }
}

fun NavHostController.navigateToLibraryDestination(destination: LibraryDestination) {
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
