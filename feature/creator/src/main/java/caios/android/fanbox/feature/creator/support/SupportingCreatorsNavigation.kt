package caios.android.fanbox.feature.creator.support

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.ui.animation.NavigateAnimation
import caios.android.fanbox.core.ui.extensition.navigateWithLog

const val SupportingCreatorsRoute = "supportingCreators"

fun NavController.navigateToSupportingCreators(navOptions: NavOptions? = null) {
    this.navigateWithLog(SupportingCreatorsRoute, navOptions)
}

fun NavGraphBuilder.supportingCreatorsScreen(
    navigateToCreatorPlans: (CreatorId) -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
    navigateToFanCard: (CreatorId) -> Unit,
    terminate: () -> Unit,
) {
    composable(
        route = SupportingCreatorsRoute,
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        SupportingCreatorsRoute(
            modifier = Modifier.fillMaxSize(),
            navigateToCreatorPlans = navigateToCreatorPlans,
            navigateToCreatorPosts = navigateToCreatorPosts,
            navigateToFanCard = navigateToFanCard,
            terminate = terminate,
        )
    }
}
