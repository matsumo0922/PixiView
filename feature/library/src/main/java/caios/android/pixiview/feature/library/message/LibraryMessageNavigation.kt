package caios.android.pixiview.feature.library.message

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import caios.android.pixiview.core.ui.animation.NavigateAnimation

const val LibraryMessageRoute = "libraryMessage"

fun NavController.navigateToLibraryMessage(navOptions: NavOptions? = null) {
    this.navigate(LibraryMessageRoute, navOptions)
}

fun NavGraphBuilder.libraryMessageScreen() {
    composable(
        route = LibraryMessageRoute,
        enterTransition = { NavigateAnimation.Library.enter },
        exitTransition = { NavigateAnimation.Library.exit },
    ) {
        LibraryMessageScreen()
    }
}
