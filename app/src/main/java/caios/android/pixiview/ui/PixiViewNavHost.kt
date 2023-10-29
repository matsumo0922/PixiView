package caios.android.pixiview.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import caios.android.pixiview.feature.library.LibraryRoute
import caios.android.pixiview.feature.library.libraryScreen
import caios.android.pixiview.feature.post.navigateToPostDetail
import caios.android.pixiview.feature.post.postDetailScreen

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
            navigateToCreatorPlans = {},
        )

        postDetailScreen(
            terminate = { navController.popBackStack() },
        )
    }
}
