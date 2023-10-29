package caios.android.pixiview.feature.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.animation.NavigateAnimation

const val LibraryRoute = "library"

fun NavController.navigateToLibrary() {
    this.navigate(LibraryRoute)
}

fun NavGraphBuilder.libraryScreen(
    navigateToPostDetail: (postId: PostId) -> Unit,
    navigateToCreatorPlans: (creatorId: CreatorId) -> Unit,
) {
    composable(
        route = LibraryRoute,
        enterTransition = { NavigateAnimation.Library.enter },
        exitTransition = { NavigateAnimation.Library.exit },
    ) {
        LibraryScreen(
            modifier = Modifier.fillMaxSize(),
            navigateToPostDetail = navigateToPostDetail,
            navigateToCreatorPlans = navigateToCreatorPlans,
        )
    }
}
