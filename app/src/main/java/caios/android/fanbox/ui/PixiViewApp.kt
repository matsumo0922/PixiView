package caios.android.fanbox.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import caios.android.fanbox.MainViewModel
import caios.android.fanbox.core.model.UserData
import caios.android.fanbox.core.ui.component.PixiViewBackground
import caios.android.fanbox.feature.welcome.WelcomeNavHost
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import timber.log.Timber

@Composable
internal fun PixiViewApp(
    userData: UserData,
    isAgreedTeams: Boolean,
    isAllowedPermission: Boolean,
    isLoggedIn: Boolean,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(true) {
        if (userData.pixiViewId.isBlank()) {
            viewModel.initPixiViewId()
        }
    }

    LaunchedEffect(isLoggedIn) {
        Timber.d("isShowWelcomeScreen:, $isLoggedIn")
    }

    PixiViewBackground(modifier) {
        AnimatedContent(
            targetState = !isAgreedTeams || !isLoggedIn || !isAllowedPermission,
            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
            label = "isShowWelcomeScreen",
        ) {
            if (it) {
                WelcomeNavHost(
                    modifier = Modifier.fillMaxSize(),
                    isAgreedTeams = isAgreedTeams,
                    isAllowedPermission = isAllowedPermission,
                    isLoggedIn = isLoggedIn,
                    onComplete = viewModel::updateState,
                )
            } else {
                IdleScreen(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterialApi::class)
@Composable
private fun IdleScreen(
    modifier: Modifier = Modifier,
) {
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
    val bottomSheetNavigator = remember { BottomSheetNavigator(bottomSheetState) }
    val navController = rememberNavController(bottomSheetNavigator)

    PixiViewNavHost(
        modifier = modifier,
        bottomSheetNavigator = bottomSheetNavigator,
        navController = navController,
    )
}
