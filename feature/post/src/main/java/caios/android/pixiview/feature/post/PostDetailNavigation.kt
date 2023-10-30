package caios.android.pixiview.feature.post

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.animation.NavigateAnimation

const val PostDetailId = "postDetailId"
const val PostDetailRoute = "postDetail/{$PostDetailId}"

fun NavController.navigateToPostDetail(postId: PostId) {
    this.navigate("postDetail/$postId")
}

fun NavGraphBuilder.postDetailScreen(
    terminate: () -> Unit,
) {
    composable(
        route = PostDetailRoute,
        arguments = listOf(navArgument(PostDetailId) { type = NavType.StringType }),
        enterTransition = { NavigateAnimation.Vertical.enter },
        exitTransition = { NavigateAnimation.Vertical.exit },
        popEnterTransition = { NavigateAnimation.Vertical.popEnter },
        popExitTransition = { NavigateAnimation.Vertical.popExit },
    ) {
        PostDetailRoute(
            modifier = Modifier.fillMaxSize(),
            postId = PostId(it.arguments?.getString(PostDetailId) ?: ""),
            terminate = terminate,
        )
    }
}
