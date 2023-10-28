package caios.android.pixiview.feature.library.discovery

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import caios.android.pixiview.core.ui.animation.NavigateAnimation

const val LibraryDiscoveryRoute = "libraryDiscovery"

fun NavController.navigateToLibraryDiscovery(navOptions: NavOptions? = null) {
    this.navigate(LibraryDiscoveryRoute, navOptions)
}

fun NavGraphBuilder.libraryDiscoveryScreen() {
    composable(
        route = LibraryDiscoveryRoute,
        enterTransition = { NavigateAnimation.Library.enter },
        exitTransition = { NavigateAnimation.Library.exit },
    ) {
        LibraryDiscoveryScreen(
            modifier = Modifier.fillMaxSize(),
        )
    }
}