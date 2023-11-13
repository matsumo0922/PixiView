package caios.android.pixiview.feature.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.feature.library.discovery.LibraryDiscoveryRoute
import caios.android.pixiview.feature.library.discovery.libraryDiscoveryScreen
import caios.android.pixiview.feature.library.home.LibraryHomeRoute
import caios.android.pixiview.feature.library.home.libraryHomeScreen
import caios.android.pixiview.feature.library.message.LibraryMessageRoute
import caios.android.pixiview.feature.library.message.libraryMessageScreen
import caios.android.pixiview.feature.library.notify.LibraryNotifyRoute
import caios.android.pixiview.feature.library.notify.libraryNotifyScreen

@Composable
fun LibraryNavHost(
    openDrawer: () -> Unit,
    navigateToPostDetail: (postId: PostId) -> Unit,
    navigateToCreatorTop: (creatorId: CreatorId) -> Unit,
    navigateToCreatorPlans: (creatorId: CreatorId) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = LibraryHomeRoute,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        libraryHomeScreen(
            openDrawer = openDrawer,
            navigateToPostDetail = navigateToPostDetail,
            navigateToCreatorTop = navigateToCreatorTop,
            navigateToCreatorPlans = navigateToCreatorPlans,
        )

        libraryDiscoveryScreen(
            openDrawer = openDrawer,
            navigateToCreatorPlans = navigateToCreatorPlans,
        )

        libraryNotifyScreen(
            openDrawer = openDrawer,
            navigateToPostDetail = navigateToPostDetail,
        )

        libraryMessageScreen(
            openDrawer = openDrawer,
            navigateToCreatorPlans = navigateToCreatorPlans,
        )
    }
}

internal val LibraryRoutes = listOf(
    LibraryHomeRoute,
    LibraryDiscoveryRoute,
    LibraryNotifyRoute,
    LibraryMessageRoute,
)
