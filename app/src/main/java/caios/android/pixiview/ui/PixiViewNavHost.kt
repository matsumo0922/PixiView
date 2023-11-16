package caios.android.pixiview.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import caios.android.pixiview.feature.creator.support.navigateToSupportingCreators
import caios.android.pixiview.feature.creator.support.supportingCreatorsScreen
import caios.android.pixiview.feature.creator.top.creatorTopScreen
import caios.android.pixiview.feature.creator.top.navigateToCreatorTop
import caios.android.pixiview.feature.library.LibraryRoute
import caios.android.pixiview.feature.library.libraryScreen
import caios.android.pixiview.feature.post.detail.navigateToPostDetail
import caios.android.pixiview.feature.post.detail.postDetailScreen
import caios.android.pixiview.feature.post.image.navigateToPostImage
import caios.android.pixiview.feature.post.image.postImageScreen

@Composable
internal fun PixiViewNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = LibraryRoute,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        libraryScreen(
            navigateToPostDetail = { navController.navigateToPostDetail(it) },
            navigateToCreatorPosts = { navController.navigateToCreatorTop(it) },
            navigateToCreatorPlans = { navController.navigateToCreatorTop(it) },
            navigateToFollowerCreators = { },
            navigateToSupportingCreators = { navController.navigateToSupportingCreators() },
        )

        postDetailScreen(
            navigateToPostDetail = { navController.navigateToPostDetail(it) },
            navigateToPostImage = { postId, index -> navController.navigateToPostImage(postId, index) },
            navigateToCreatorPosts = { navController.navigateToCreatorTop(it, isPosts = true) },
            navigateToCreatorPlans = { navController.navigateToCreatorTop(it) },
            terminate = { navController.popBackStack() },
        )

        postImageScreen(
            terminate = { navController.popBackStack() },
        )

        creatorTopScreen(
            navigateToPostDetail = { navController.navigateToPostDetail(it) },
            terminate = { navController.popBackStack() },
        )

        supportingCreatorsScreen(
            navigateToCreatorPlans = { navController.navigateToCreatorTop(it) },
            navigateToCreatorPosts = { navController.navigateToCreatorTop(it, isPosts = true) },
            terminate = { navController.popBackStack() },
        )
    }
}
