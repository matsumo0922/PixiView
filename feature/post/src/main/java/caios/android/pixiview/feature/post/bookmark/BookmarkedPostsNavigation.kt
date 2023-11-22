package caios.android.pixiview.feature.post.bookmark

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.animation.NavigateAnimation

const val BookmarkedPostsRoute = "bookmarkedPosts"

fun NavController.navigateToBookmarkedPosts() {
    this.navigate(BookmarkedPostsRoute)
}

fun NavGraphBuilder.bookmarkedPostsScreen(
    navigateToPostDetail: (PostId) -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
    navigateToCreatorPlans: (CreatorId) -> Unit,
    terminate: () -> Unit,
) {
    composable(
        route = BookmarkedPostsRoute,
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        BookmarkedPostsRoute(
            navigateToPostDetail = navigateToPostDetail,
            navigateToCreatorPosts = navigateToCreatorPosts,
            navigateToCreatorPlans = navigateToCreatorPlans,
            terminate = terminate,
        )
    }
}
