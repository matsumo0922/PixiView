package caios.android.fanbox.feature.welcome.login.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.fanbox.core.model.ScreenState
import caios.android.fanbox.core.model.ThemeConfig
import caios.android.fanbox.core.ui.AsyncLoadContents
import caios.android.fanbox.core.ui.component.PixiViewBackground
import caios.android.fanbox.core.ui.theme.DarkDefaultColorScheme
import caios.android.fanbox.core.ui.theme.LightDefaultColorScheme
import caios.android.fanbox.core.ui.theme.PixiViewTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val screenState by viewModel.screenState.collectAsStateWithLifecycle()
            val systemUiController = rememberSystemUiController()
            val shouldUseDarkTheme = shouldUseDarkTheme(screenState)

            DisposableEffect(systemUiController, shouldUseDarkTheme) {
                systemUiController.systemBarsDarkContentEnabled = !shouldUseDarkTheme
                onDispose {}
            }

            AsyncLoadContents(
                modifier = Modifier.fillMaxSize(),
                screenState = screenState,
                containerColor = if (shouldUseDarkTheme) DarkDefaultColorScheme.surface else LightDefaultColorScheme.surface,
            ) {
                PixiViewTheme(
                    themeColorConfig = it.userData.themeColorConfig,
                    shouldUseDarkTheme = shouldUseDarkTheme,
                    enableDynamicTheme = shouldUseDynamicColor(screenState),
                ) {
                    PixiViewBackground {
                        LoginScreen(
                            modifier = Modifier.fillMaxSize(),
                            onUpdateCookie = viewModel::updateCookie,
                            onDismiss = ::dismiss,
                        )
                    }
                }
            }
        }
    }

    private fun dismiss(isSuccess: Boolean) {
        setResult(if (isSuccess) RESULT_OK else RESULT_CANCELED)
        finish()
    }

    @Composable
    private fun shouldUseDarkTheme(screenState: ScreenState<LoginUiState>): Boolean {
        return when (screenState) {
            is ScreenState.Idle -> when (screenState.data.userData.themeConfig) {
                ThemeConfig.Light -> false
                ThemeConfig.Dark -> true
                else -> isSystemInDarkTheme()
            }

            else -> isSystemInDarkTheme()
        }
    }

    @Composable
    private fun shouldUseDynamicColor(screenState: ScreenState<LoginUiState>): Boolean {
        return when (screenState) {
            is ScreenState.Idle -> screenState.data.userData.isDynamicColor
            else -> false
        }
    }
}
