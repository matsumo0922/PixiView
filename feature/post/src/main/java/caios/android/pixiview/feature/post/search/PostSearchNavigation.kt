package caios.android.pixiview.feature.post.search

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.animation.NavigateAnimation


const val PostSearchQueryStr = "postSearchQuery"
const val PostSearchRoute = "postSearch/{$PostSearchQueryStr}"

fun NavController.navigateToPostSearch(creatorId: CreatorId? = null, creatorQuery: String? = null, tag: String? = null) {
    val query = buildQuery(creatorId, creatorQuery, tag)
    val route = if (parseQuery(query).mode != PostSearchMode.Unknown) "postSearch/$query" else "postSearch/pixiViewUnknown"

    this.navigate(route)
}

fun NavGraphBuilder.postSearchScreen(
    navigateToPostDetail: (PostId) -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
    navigateToCreatorPlans: (CreatorId) -> Unit,
    terminate: () -> Unit,
) {
    composable(
        route = PostSearchRoute,
        arguments = listOf(
            navArgument(PostSearchQueryStr) {
                type = NavType.StringType
                nullable = true
            }
        ),
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        var query = it.arguments?.getString(PostSearchQueryStr) ?: ""

        if (query == "pixiViewUnknown") {
            query = ""
        }

        PostSearchRoute(
            query = query,
            navigateToPostDetail = navigateToPostDetail,
            navigateToCreatorPosts = navigateToCreatorPosts,
            navigateToCreatorPlans = navigateToCreatorPlans,
            terminate = terminate,
        )
    }
}
