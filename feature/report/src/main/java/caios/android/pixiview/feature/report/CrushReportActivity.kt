package caios.android.pixiview.feature.report

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import caios.android.pixiview.core.ui.theme.PixiViewTheme
import caios.android.pixiview.core.model.ThemeConfig
import caios.android.pixiview.core.common.util.ToastUtil
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.component.PixiViewBackground
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import caios.android.pixiview.feature.report.R

@AndroidEntryPoint
class CrushReportActivity : ComponentActivity() {

    private val viewModel by viewModels<CrushReportViewModel>()
    private val clipboardManager by lazy { getSystemService(CLIPBOARD_SERVICE) as ClipboardManager }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        var screenState: ScreenState<UserData> by mutableStateOf(ScreenState.Loading)
        val report = intent.getStringExtra("report").toString()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenState.onEach { screenState = it }.collect()
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        setContent {
            val systemUiController = rememberSystemUiController()
            val shouldUseDarkTheme = shouldUseDarkTheme(screenState)

            DisposableEffect(systemUiController, shouldUseDarkTheme) {
                systemUiController.systemBarsDarkContentEnabled = !shouldUseDarkTheme
                onDispose {}
            }

            BackHandler {
                finishAffinity()
            }

            AsyncLoadContents(
                modifier = Modifier.fillMaxSize(),
                screenState = screenState,
            ) { userData ->
                PixiViewTheme(
                    themeColorConfig = userData.themeColorConfig,
                    shouldUseDarkTheme = shouldUseDarkTheme,
                    enableDynamicTheme = shouldUseDynamicColor(screenState),
                ) {
                    PixiViewBackground {
                        CrushReportScreen(
                            modifier = Modifier.fillMaxSize(),
                            report = report,
                            onClickCopy = {
                                clipboardManager.setPrimaryClip(ClipData.newPlainText("PixiView Crush Report", it))

                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                    ToastUtil.show(this@CrushReportActivity, R.string.report_crush_copy)
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) finishAffinity()
    }

    @Composable
    private fun shouldUseDarkTheme(screenState: ScreenState<UserData>): Boolean {
        return when (screenState) {
            is ScreenState.Idle -> when (screenState.data.themeConfig) {
                ThemeConfig.Light -> false
                ThemeConfig.Dark -> true
                else -> isSystemInDarkTheme()
            }
            else -> isSystemInDarkTheme()
        }
    }

    @Composable
    private fun shouldUseDynamicColor(screenState: ScreenState<UserData>): Boolean {
        return when (screenState) {
            is ScreenState.Idle -> screenState.data.isDynamicColor
            else -> false
        }
    }
}
