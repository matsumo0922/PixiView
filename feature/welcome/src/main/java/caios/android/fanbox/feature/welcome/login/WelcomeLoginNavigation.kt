package caios.android.fanbox.feature.welcome.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import caios.android.fanbox.core.ui.animation.NavigateAnimation

const val WelcomeLoginRoute = "welcomeLogin"

fun NavController.navigateToWelcomeLogin() {
    this.navigate(WelcomeLoginRoute)
}

fun NavGraphBuilder.welcomeLoginScreen(
    navigateToWelcomePermission: () -> Unit,
) {
    composable(
        route = WelcomeLoginRoute,
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        WelcomeLoginScreen(
            modifier = Modifier.fillMaxSize(),
            navigateToWelcomePermission = navigateToWelcomePermission,
        )
    }
}
