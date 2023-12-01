package caios.android.fanbox.feature.library.discovery

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.ui.animation.NavigateAnimation

const val LibraryDiscoveryRoute = "libraryDiscovery"

fun NavController.navigateToLibraryDiscovery(navOptions: NavOptions? = null) {
    this.navigate(LibraryDiscoveryRoute, navOptions)
}

fun NavGraphBuilder.libraryDiscoveryScreen(
    openDrawer: () -> Unit,
    navigateToPostSearch: () -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
) {
    composable(
        route = LibraryDiscoveryRoute,
        enterTransition = { NavigateAnimation.Library.enter },
        exitTransition = { NavigateAnimation.Library.exit },
    ) {
        LibraryDiscoveryRoute(
            modifier = Modifier.fillMaxSize(),
            openDrawer = openDrawer,
            navigateToPostSearch = navigateToPostSearch,
            navigateToCreatorPosts = navigateToCreatorPosts,
        )
    }
}
