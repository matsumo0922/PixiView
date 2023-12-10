package caios.android.fanbox.feature.post.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.model.fanbox.id.PostId
import caios.android.fanbox.core.ui.animation.NavigateAnimation
import caios.android.fanbox.core.ui.view.SimpleAlertContents

const val PostDetailId = "postDetailId"
const val PostDetailType = "postDetailPagingType"
const val PostDetailRoute = "postDetail/{$PostDetailId}/{$PostDetailType}"

fun NavController.navigateToPostDetail(postId: PostId, pagingType: PostDetailPagingType) {
    this.navigate("postDetail/$postId/${pagingType.name}")
}

fun NavGraphBuilder.postDetailScreen(
    navigateToPostSearch: (String, CreatorId) -> Unit,
    navigateToPostDetail: (PostId) -> Unit,
    navigateToPostImage: (PostId, Int) -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
    navigateToCreatorPlans: (CreatorId) -> Unit,
    navigateToCommentDeleteDialog: (SimpleAlertContents, () -> Unit) -> Unit,
    terminate: () -> Unit,
) {
    composable(
        route = PostDetailRoute,
        arguments = listOf(
            navArgument(PostDetailId) { type = NavType.StringType },
            navArgument(PostDetailType) { type = NavType.StringType },
        ),
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        PostDetailRoute(
            modifier = Modifier.fillMaxSize(),
            postId = PostId(it.arguments?.getString(PostDetailId) ?: ""),
            type = PostDetailPagingType.valueOf(it.arguments?.getString(PostDetailType) ?: PostDetailPagingType.Unknown.name),
            navigateToPostSearch = navigateToPostSearch,
            navigateToPostDetail = navigateToPostDetail,
            navigateToPostImage = navigateToPostImage,
            navigateToCreatorPosts = navigateToCreatorPosts,
            navigateToCreatorPlans = navigateToCreatorPlans,
            navigateToCommentDeleteDialog = navigateToCommentDeleteDialog,
            terminate = terminate,
        )
    }
}

enum class PostDetailPagingType {
    Home,
    Supported,
    Creator,
    Search,
    Unknown,
}
