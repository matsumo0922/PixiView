package caios.android.fanbox.feature.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.model.fanbox.id.PostId
import caios.android.fanbox.feature.library.discovery.LibraryDiscoveryRoute
import caios.android.fanbox.feature.library.discovery.libraryDiscoveryScreen
import caios.android.fanbox.feature.library.home.LibraryHomeRoute
import caios.android.fanbox.feature.library.home.libraryHomeScreen
import caios.android.fanbox.feature.library.message.LibraryMessageRoute
import caios.android.fanbox.feature.library.message.libraryMessageScreen
import caios.android.fanbox.feature.library.notify.LibraryNotifyRoute
import caios.android.fanbox.feature.library.notify.libraryNotifyScreen

@Composable
fun LibraryNavHost(
    openDrawer: () -> Unit,
    navigateToPostSearch: () -> Unit,
    navigateToPostDetailFromHome: (postId: PostId) -> Unit,
    navigateToPostDetailFromSupported: (postId: PostId) -> Unit,
    navigateToCreatorPosts: (creatorId: CreatorId) -> Unit,
    navigateToCreatorPlans: (creatorId: CreatorId) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = LibraryHomeRoute,
    applyOtherRoutes: NavGraphBuilder.() -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        libraryHomeScreen(
            openDrawer = openDrawer,
            navigateToPostDetailFromHome = navigateToPostDetailFromHome,
            navigateToPostDetailFromSupported = navigateToPostDetailFromSupported,
            navigateToCreatorPosts = navigateToCreatorPosts,
            navigateToCreatorPlans = navigateToCreatorPlans,
        )

        libraryDiscoveryScreen(
            openDrawer = openDrawer,
            navigateToPostSearch = navigateToPostSearch,
            navigateToCreatorPosts = navigateToCreatorPosts,
        )

        libraryNotifyScreen(
            openDrawer = openDrawer,
            navigateToPostDetail = navigateToPostDetailFromHome,
        )

        libraryMessageScreen(
            openDrawer = openDrawer,
            navigateToCreatorPosts = navigateToCreatorPosts,
        )

        applyOtherRoutes.invoke(this)
    }
}

internal val LibraryRoutes = listOf(
    LibraryHomeRoute,
    LibraryDiscoveryRoute,
    LibraryNotifyRoute,
    LibraryMessageRoute,
)
