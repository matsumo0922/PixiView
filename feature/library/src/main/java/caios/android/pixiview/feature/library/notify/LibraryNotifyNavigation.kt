package caios.android.pixiview.feature.library.notify

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import caios.android.pixiview.core.ui.animation.NavigateAnimation

const val LibraryNotifyRoute = "libraryNotify"

fun NavController.navigateToLibraryNotify(navOptions: NavOptions? = null) {
    this.navigate(LibraryNotifyRoute, navOptions)
}

fun NavGraphBuilder.libraryNotifyScreen() {
    composable(
        route = LibraryNotifyRoute,
        enterTransition = { NavigateAnimation.Library.enter },
        exitTransition = { NavigateAnimation.Library.exit },
    ) {
        LibraryNotifyScreen()
    }
}
