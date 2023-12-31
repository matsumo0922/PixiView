package caios.android.fanbox.feature.library.message

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.ui.animation.NavigateAnimation
import caios.android.fanbox.core.ui.extensition.navigateWithLog

const val LibraryMessageRoute = "libraryMessage"

fun NavController.navigateToLibraryMessage(navOptions: NavOptions? = null) {
    this.navigateWithLog(LibraryMessageRoute, navOptions)
}

fun NavGraphBuilder.libraryMessageScreen(
    openDrawer: () -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
) {
    composable(
        route = LibraryMessageRoute,
        enterTransition = { NavigateAnimation.Library.enter },
        exitTransition = { NavigateAnimation.Library.exit },
    ) {
        LibraryMessageRoute(
            modifier = Modifier.fillMaxSize(),
            openDrawer = openDrawer,
            navigateToCreatorPosts = navigateToCreatorPosts,
        )
    }
}
