package caios.android.fanbox.feature.creator.follow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.ui.animation.NavigateAnimation
import caios.android.fanbox.core.ui.extensition.navigateWithLog

const val FollowingCreatorsRoute = "followingCreators"

fun NavController.navigateToFollowingCreators(navOptions: NavOptions? = null) {
    this.navigateWithLog(FollowingCreatorsRoute, navOptions)
}

fun NavGraphBuilder.followingCreatorsScreen(
    navigateToCreatorPlans: (CreatorId) -> Unit,
    terminate: () -> Unit,
) {
    composable(
        route = FollowingCreatorsRoute,
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        FollowingCreatorsRoute(
            modifier = Modifier.fillMaxSize(),
            navigateToCreatorPlans = navigateToCreatorPlans,
            terminate = terminate,
        )
    }
}
