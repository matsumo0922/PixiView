package caios.android.fanbox.feature.setting.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import caios.android.fanbox.core.ui.animation.NavigateAnimation
import caios.android.fanbox.core.ui.extensition.navigateWithLog

const val SettingThemeDialogRoute = "SettingTheme"

fun NavController.navigateToSettingTheme() {
    this.navigateWithLog(SettingThemeDialogRoute)
}

fun NavGraphBuilder.settingThemeScreen(
    navigateToBillingPlus: () -> Unit,
    terminate: () -> Unit,
) {
    composable(
        route = SettingThemeDialogRoute,
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        SettingThemeRoute(
            modifier = Modifier.fillMaxSize(),
            navigateToBillingPlus = navigateToBillingPlus,
            terminate = terminate,
        )
    }
}
