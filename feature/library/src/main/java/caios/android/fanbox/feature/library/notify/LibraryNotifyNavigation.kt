package caios.android.fanbox.feature.library.notify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import caios.android.fanbox.core.model.fanbox.id.PostId
import caios.android.fanbox.core.ui.animation.NavigateAnimation
import caios.android.fanbox.core.ui.extensition.navigateWithLog

const val LibraryNotifyRoute = "libraryNotify"

fun NavController.navigateToLibraryNotify(navOptions: NavOptions? = null) {
    this.navigateWithLog(LibraryNotifyRoute, navOptions)
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
