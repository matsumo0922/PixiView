package caios.android.fanbox.feature.creator.payment

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.ui.animation.NavigateAnimation
import caios.android.fanbox.core.ui.extensition.navigateWithLog

const val PaymentsRoute = "payments"

fun NavController.navigateToPayments(navOptions: NavOptions? = null) {
    this.navigateWithLog(PaymentsRoute, navOptions)
}

fun NavGraphBuilder.paymentsScreen(
    navigateToCreatorPosts: (CreatorId) -> Unit,
    terminate: () -> Unit,
) {
    composable(
        route = PaymentsRoute,
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        PaymentsRoute(
            modifier = Modifier.fillMaxSize(),
            navigateToCreatorPosts = navigateToCreatorPosts,
            terminate = terminate,
        )
    }
}
