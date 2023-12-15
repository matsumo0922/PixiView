package caios.android.fanbox.feature.about.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import caios.android.fanbox.core.model.Version
import caios.android.fanbox.core.ui.animation.NavigateAnimation
import caios.android.fanbox.core.ui.extensition.navigateWithLog
import kotlinx.collections.immutable.ImmutableList

const val AboutRoute = "about"

fun NavController.navigateToAbout() {
    this.navigateWithLog(AboutRoute)
}

fun NavGraphBuilder.aboutScreen(
    navigateToVersionHistory: (ImmutableList<Version>) -> Unit,
    navigateToDonate: () -> Unit,
    terminate: () -> Unit,
) {
    composable(
        route = AboutRoute,
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        AboutRoute(
            modifier = Modifier.fillMaxSize(),
            navigateToVersionHistory = navigateToVersionHistory,
            navigateToDonate = navigateToDonate,
            terminate = terminate,
        )
    }
}
