package caios.android.fanbox.feature.setting.oss

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import caios.android.fanbox.core.ui.animation.NavigateAnimation
import caios.android.fanbox.core.ui.extensition.navigateWithLog

const val SettingLicenseRoute = "SettingLicense"

fun NavController.navigateToSettingLicense() {
    this.navigateWithLog(SettingLicenseRoute)
}

fun NavGraphBuilder.settingLicenseScreen(
    terminate: () -> Unit,
) {
    composable(
        route = SettingLicenseRoute,
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        SettingLicenseScreen(
            modifier = Modifier.fillMaxSize(),
            terminate = terminate,
        )
    }
}
