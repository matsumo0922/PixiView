package caios.android.fanbox.feature.welcome.permission

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import caios.android.fanbox.core.ui.animation.NavigateAnimation
import caios.android.fanbox.core.ui.extensition.navigateWithLog

const val WelcomePermissionRoute = "welcomePermission"

fun NavController.navigateToWelcomePermission() {
    this.navigateWithLog(WelcomePermissionRoute)
}

fun NavGraphBuilder.welcomePermissionScreen(
    navigateToHome: () -> Unit,
) {
    composable(
        route = WelcomePermissionRoute,
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        WelcomePermissionScreen(
            modifier = Modifier.fillMaxSize(),
            navigateToHome = navigateToHome,
        )
    }
}
