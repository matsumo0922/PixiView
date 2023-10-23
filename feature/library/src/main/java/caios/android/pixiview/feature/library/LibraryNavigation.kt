package caios.android.pixiview.feature.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import caios.android.pixiview.core.ui.animation.NavigateAnimation
import caios.android.pixiview.feature.library.home.LibraryHomeRoute

const val LibraryRoute = "library"

fun NavController.navigateToLibrary() {
    this.navigate(LibraryRoute)
}

fun NavGraphBuilder.libraryScreen() {
    composable(
        route = LibraryRoute,
        enterTransition = { NavigateAnimation.Library.enter },
        exitTransition = { NavigateAnimation.Library.exit },
    ) {
        LibraryScreen(Modifier.fillMaxSize())
    }
}
