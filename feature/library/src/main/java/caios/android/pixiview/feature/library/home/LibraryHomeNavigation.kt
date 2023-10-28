package caios.android.pixiview.feature.library.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import caios.android.pixiview.core.ui.animation.NavigateAnimation

const val LibraryHomeRoute = "libraryHome"

fun NavController.navigateToLibraryHome(navOptions: NavOptions? = null) {
    this.navigate(LibraryHomeRoute, navOptions)
}

fun NavGraphBuilder.libraryHomeScreen() {
    composable(
        route = LibraryHomeRoute,
        enterTransition = { NavigateAnimation.Library.enter },
        exitTransition = { NavigateAnimation.Library.exit },
    ) {
        LibraryHomeScreen(
            modifier = Modifier.fillMaxSize(),
        )
    }
}
