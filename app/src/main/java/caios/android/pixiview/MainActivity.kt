package caios.android.pixiview

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.ThemeConfig
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.theme.PixiViewTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val screenState by viewModel.screenState.collectAsStateWithLifecycle()
            val systemUiController = rememberSystemUiController()
            val shouldUseDarkTheme = shouldUseDarkTheme(screenState)

            LaunchedEffect(screenState) {
                splashScreen.setKeepOnScreenCondition {
                    when (screenState) {
                        is ScreenState.Loading -> true
                        else -> false
                    }
                }
            }

            DisposableEffect(systemUiController, shouldUseDarkTheme) {
                systemUiController.systemBarsDarkContentEnabled = !shouldUseDarkTheme
                onDispose {}
            }

            AsyncLoadContents(
                modifier = Modifier.fillMaxSize(),
                screenState = screenState,
            ) {
                val isAgreedTeams = remember { it.userData.isAgreedPrivacyPolicy && it.userData.isAgreedTermsOfService }
                val isAllowedPermission = remember { isAllowedPermission() }

                PixiViewTheme(
                    themeColorConfig = it.userData.themeColorConfig,
                    shouldUseDarkTheme = shouldUseDarkTheme,
                    enableDynamicTheme = shouldUseDynamicColor(screenState),
                ) {
                    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                        Text(
                            text = "Hello, World!",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun shouldUseDarkTheme(screenState: ScreenState<MainUiState>): Boolean {
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
    private fun shouldUseDynamicColor(screenState: ScreenState<MainUiState>): Boolean {
        return when (screenState) {
            is ScreenState.Idle -> screenState.data.userData.isDynamicColor
            else -> false
        }
    }

    private fun isAllowedPermission(): Boolean {
        val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO,
            )
        } else {
            listOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        return storagePermission.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
