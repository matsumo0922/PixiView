package caios.android.fanbox.feature.post.search

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.model.fanbox.id.PostId
import caios.android.fanbox.core.ui.animation.NavigateAnimation
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder

const val PostSearchQueryStr = "postSearchQuery"
const val PostSearchRoute = "postSearch/{$PostSearchQueryStr}"

fun NavController.navigateToPostSearch(creatorId: CreatorId? = null, creatorQuery: String? = null, tag: String? = null) {
    val query = URLEncoder.encode(buildQuery(creatorId, creatorQuery, tag), Charsets.UTF_8.name())
    val encodedQuery = URLEncoder.encode(query, Charsets.UTF_8.name())
    val route = if (parseQuery(query).mode != PostSearchMode.Unknown) "postSearch/$encodedQuery" else "postSearch/pixiViewUnknown"

    this.navigate(route)
}

fun NavGraphBuilder.postSearchScreen(
    navigateToPostSearch: (CreatorId?, String?, String?) -> Unit,
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
            },
        ),
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        var query = URLDecoder.decode(it.arguments?.getString(PostSearchQueryStr) ?: "", Charsets.UTF_8.name())

        if (query == "pixiViewUnknown") {
            query = ""
        }

        PostSearchRoute(
            query = query,
            navigateToPostSearch = navigateToPostSearch,
            navigateToPostDetail = navigateToPostDetail,
            navigateToCreatorPosts = navigateToCreatorPosts,
            navigateToCreatorPlans = navigateToCreatorPlans,
            terminate = terminate,
        )
    }
}
