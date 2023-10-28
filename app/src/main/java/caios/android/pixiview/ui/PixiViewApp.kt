package caios.android.pixiview.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import caios.android.pixiview.MainViewModel
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.ui.component.PixiViewBackground
import caios.android.pixiview.feature.welcome.WelcomeNavHost

@Composable
internal fun PixiViewApp(
    userData: UserData,
    isAgreedTeams: Boolean,
    isAllowedPermission: Boolean,
    isLoggedIn: Boolean,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    var isShowWelcomeScreen by remember { mutableStateOf(!isAgreedTeams || !isLoggedIn || !isAllowedPermission) }

    LaunchedEffect(true) {
        if (userData.pixiViewId.isBlank()) {
            viewModel.initPixiViewId()
        }
    }

    PixiViewBackground(modifier) {
        AnimatedContent(
            targetState = isShowWelcomeScreen && (!isAgreedTeams || !isLoggedIn || !isAllowedPermission),
            label = "isShowWelcomeScreen",
        ) {
            if (it) {
                WelcomeNavHost(
                    modifier = Modifier.fillMaxSize(),
                    isAgreedTeams = isAgreedTeams,
                    isAllowedPermission = isAllowedPermission,
                    isLoggedIn = isLoggedIn,
                    onComplete = { isShowWelcomeScreen = false },
                )
            } else {
                IdleScreen(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun IdleScreen(
    modifier: Modifier = Modifier,
) {
    PixiViewNavHost(
        modifier = modifier,
    )
}
