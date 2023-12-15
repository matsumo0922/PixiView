package caios.android.fanbox.feature.welcome.top

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import caios.android.fanbox.core.ui.animation.NavigateAnimation
import caios.android.fanbox.core.ui.extensition.navigateWithLog

const val WelcomeTopRoute = "welcomeTop"

fun NavController.navigateToWelcomeTop() {
    this.navigateWithLog(WelcomeTopRoute)
}

fun NavGraphBuilder.welcomeTopScreen(
    navigateToWelcomePlus: () -> Unit,
) {
    composable(
        route = WelcomeTopRoute,
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        WelcomeTopScreen(
            modifier = Modifier.fillMaxSize(),
            navigateToWelcomePlus = navigateToWelcomePlus,
        )
    }
}
