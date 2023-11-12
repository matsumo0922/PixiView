package caios.android.pixiview.feature.library.notify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.animation.NavigateAnimation

const val LibraryNotifyRoute = "libraryNotify"

fun NavController.navigateToLibraryNotify(navOptions: NavOptions? = null) {
    this.navigate(LibraryNotifyRoute, navOptions)
}

fun NavGraphBuilder.libraryNotifyScreen(
    openDrawer: () -> Unit,
    navigateToPostDetail: (PostId) -> Unit,
) {
    composable(
        route = LibraryNotifyRoute,
        enterTransition = { NavigateAnimation.Library.enter },
        exitTransition = { NavigateAnimation.Library.exit },
    ) {
        LibraryNotifyRoute(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            openDrawer = openDrawer,
            navigateToPostDetail = navigateToPostDetail,
        )
    }
}
