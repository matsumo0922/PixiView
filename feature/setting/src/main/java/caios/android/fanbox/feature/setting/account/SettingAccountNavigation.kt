package caios.android.fanbox.feature.setting.account

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import caios.android.fanbox.core.ui.animation.NavigateAnimation

const val SettingAccountRoute = "SettingAccount"

fun NavController.navigateToSettingAccount() {
    this.navigate(SettingAccountRoute)
}

fun NavGraphBuilder.settingAccountScreen(
    terminate: () -> Unit,
) {
    composable(
        route = SettingAccountRoute,
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
    }
}
