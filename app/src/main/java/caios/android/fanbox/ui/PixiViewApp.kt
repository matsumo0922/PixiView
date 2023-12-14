package caios.android.fanbox.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import caios.android.fanbox.MainViewModel
import caios.android.fanbox.core.model.UserData
import caios.android.fanbox.core.ui.component.PixiViewBackground
import caios.android.fanbox.core.ui.extensition.LocalNavigationType
import caios.android.fanbox.core.ui.extensition.NavigationType
import caios.android.fanbox.core.ui.extensition.PixiViewNavigationType
import caios.android.fanbox.feature.welcome.WelcomeNavHost
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import timber.log.Timber

@Composable
internal fun PixiViewApp(
    userData: UserData,
    windowSize: WindowWidthSizeClass,
    isAgreedTeams: Boolean,
    isAllowedPermission: Boolean,
    isLoggedIn: Boolean,
    viewModel: MainViewModel,
    onCompleteWelcome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigationType = when (windowSize) {
        WindowWidthSizeClass.Medium -> PixiViewNavigationType.NavigationRail
        WindowWidthSizeClass.Expanded -> PixiViewNavigationType.PermanentNavigationDrawer
        else -> PixiViewNavigationType.BottomNavigation
    }

    LaunchedEffect(true) {
        if (userData.pixiViewId.isBlank()) {
            viewModel.initPixiViewId()
        }
    }

    LaunchedEffect(isLoggedIn) {
        Timber.d("isShowWelcomeScreen:, $isLoggedIn")
    }

    CompositionLocalProvider(LocalNavigationType provides NavigationType(navigationType)) {
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
                        onComplete = { onCompleteWelcome.invoke() },
                    )
                } else {
                    IdleScreen(
                        modifier = Modifier.fillMaxSize(),
                        navigationType = navigationType,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterialApi::class)
@Composable
private fun IdleScreen(
    navigationType: PixiViewNavigationType,
    modifier: Modifier = Modifier,
) {
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
    val bottomSheetNavigator = remember { BottomSheetNavigator(bottomSheetState) }

    PixiViewNavHost(
        modifier = modifier,
        bottomSheetNavigator = bottomSheetNavigator,
        navigationType = navigationType,
    )
}
