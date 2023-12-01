package caios.android.fanbox.feature.post.image

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import caios.android.fanbox.core.model.fanbox.id.PostId
import caios.android.fanbox.core.ui.animation.NavigateAnimation

const val PostImageId = "postImageId"
const val PostImageIndex = "postImageIndex"
const val PostImageRoute = "postImage/{$PostImageId}/{$PostImageIndex}"

fun NavController.navigateToPostImage(postId: PostId, index: Int) {
    this.navigate("postImage/$postId/$index")
}

fun NavGraphBuilder.postImageScreen(
    terminate: () -> Unit,
) {
    composable(
        route = PostImageRoute,
        arguments = listOf(
            navArgument(PostImageId) { type = NavType.StringType },
            navArgument(PostImageIndex) { type = NavType.IntType },
        ),
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        PostImageRoute(
            modifier = Modifier.fillMaxSize(),
            postId = PostId(it.arguments?.getString(PostImageId) ?: ""),
            postImageIndex = it.arguments?.getInt(PostImageIndex) ?: 0,
            terminate = terminate,
        )
    }
}
