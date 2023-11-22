package caios.android.pixiview.feature.setting.top

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import caios.android.pixiview.core.ui.animation.NavigateAnimation
import caios.android.pixiview.core.ui.view.SimpleAlertContents

const val SettingTopRoute = "settingTop"

fun NavController.navigateToSettingTop() {
    this.navigate(SettingTopRoute)
}

fun NavGraphBuilder.settingTopScreen(
    navigateToSettingTheme: () -> Unit,
    navigateToLogoutDialog: (SimpleAlertContents, () -> Unit) -> Unit,
    navigateToOpenSourceLicense: () -> Unit,
    navigateToSettingDeveloper: () -> Unit,
    terminate: () -> Unit,
) {
    composable(
        route = SettingTopRoute,
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        SettingTopRoute(
            modifier = Modifier.fillMaxSize(),
            navigateToSettingTheme = navigateToSettingTheme,
            navigateToOpenSourceLicense = navigateToOpenSourceLicense,
            navigateToLogoutDialog = navigateToLogoutDialog,
            navigateToSettingDeveloper = navigateToSettingDeveloper,
            terminate = terminate,
        )
    }
}
