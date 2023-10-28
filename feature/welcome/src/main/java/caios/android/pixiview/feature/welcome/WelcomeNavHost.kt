package caios.android.pixiview.feature.welcome

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import caios.android.pixiview.feature.welcome.login.WelcomeLoginRoute
import caios.android.pixiview.feature.welcome.login.navigateToWelcomeLogin
import caios.android.pixiview.feature.welcome.login.welcomeLoginScreen
import caios.android.pixiview.feature.welcome.permission.WelcomePermissionRoute
import caios.android.pixiview.feature.welcome.permission.navigateToWelcomePermission
import caios.android.pixiview.feature.welcome.permission.welcomePermissionScreen
import caios.android.pixiview.feature.welcome.top.WelcomeTopRoute
import caios.android.pixiview.feature.welcome.top.welcomeTopScreen

@Composable
fun WelcomeNavHost(
    onComplete: () -> Unit,
    isAgreedTeams: Boolean,
    isAllowedPermission: Boolean,
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val startDestination = when {
        !isAgreedTeams -> WelcomeTopRoute
        !isLoggedIn -> WelcomeLoginRoute
        !isAllowedPermission -> WelcomePermissionRoute
        else -> WelcomeTopRoute
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        welcomeTopScreen(
            navigateToWelcomePlus = { navController.navigateToWelcomeLogin() },
        )

        welcomeLoginScreen(
            navigateToWelcomePermission = { navController.navigateToWelcomePermission() },
        )

        welcomePermissionScreen(
            navigateToHome = onComplete,
        )
    }
}
