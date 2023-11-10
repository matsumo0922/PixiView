package caios.android.pixiview.feature.post.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.animation.NavigateAnimation

const val PostDetailId = "postDetailId"
const val PostDetailRoute = "postDetail/{$PostDetailId}"

fun NavController.navigateToPostDetail(postId: PostId) {
    this.navigate("postDetail/$postId")
}

fun NavGraphBuilder.postDetailScreen(
    navigateToPostDetail: (PostId) -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
    navigateToCreatorPlans: (CreatorId) -> Unit,
    terminate: () -> Unit,
) {
    composable(
        route = PostDetailRoute,
        arguments = listOf(navArgument(PostDetailId) { type = NavType.StringType }),
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        PostDetailRoute(
            modifier = Modifier.fillMaxSize(),
            postId = PostId(it.arguments?.getString(PostDetailId) ?: ""),
            navigateToPostDetail = navigateToPostDetail,
            navigateToCreatorPosts = navigateToCreatorPosts,
            navigateToCreatorPlans = navigateToCreatorPlans,
            terminate = terminate,
        )
    }
}
