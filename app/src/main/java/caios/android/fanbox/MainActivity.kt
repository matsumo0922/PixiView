package caios.android.fanbox

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Process.killProcess
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.fanbox.core.common.util.ToastUtil
import caios.android.fanbox.core.model.ScreenState
import caios.android.fanbox.core.model.ThemeConfig
import caios.android.fanbox.core.model.contract.PostDownloader
import caios.android.fanbox.core.model.fanbox.FanboxPost
import caios.android.fanbox.core.model.fanbox.FanboxPostDetail
import caios.android.fanbox.core.ui.AsyncLoadContents
import caios.android.fanbox.core.ui.theme.DarkDefaultColorScheme
import caios.android.fanbox.core.ui.theme.LightDefaultColorScheme
import caios.android.fanbox.core.ui.theme.PixiViewTheme
import caios.android.fanbox.core.ui.view.BiometricDialog
import caios.android.fanbox.core.ui.view.LoadingView
import caios.android.fanbox.feature.post.service.PostDownloadService
import caios.android.fanbox.ui.PixiViewApp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity(), PostDownloader {

    private val viewModel by viewModels<MainViewModel>()
    private val biometricDialog by lazy { BiometricDialog(this) }

    private lateinit var postDownloadService: PostDownloadService
    private var isPostDownloadServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            postDownloadService = (service as PostDownloadService.PostDownloadBinder).getService()
            isPostDownloadServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isPostDownloadServiceBound = false
        }
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE,
        )

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val screenState by viewModel.screenState.collectAsStateWithLifecycle()
            val windowSize = calculateWindowSizeClass(this)
            val systemUiController = rememberSystemUiController()
            val shouldUseDarkTheme = shouldUseDarkTheme(screenState)

            splashScreen.setKeepOnScreenCondition {
                when (screenState) {
                    is ScreenState.Loading -> true
                    else -> false
                }
            }

            DisposableEffect(systemUiController, shouldUseDarkTheme) {
                systemUiController.systemBarsDarkContentEnabled = !shouldUseDarkTheme
                onDispose {}
            }

            AsyncLoadContents(
                modifier = Modifier.fillMaxSize(),
                screenState = screenState,
                containerColor = if (shouldUseDarkTheme) DarkDefaultColorScheme.surface else LightDefaultColorScheme.surface,
            ) {
                var isAgreedTeams by remember(it.userData) {
                    mutableStateOf(it.userData.isAgreedPrivacyPolicy && it.userData.isAgreedTermsOfService)
                }
                var isAllowedPermission by remember(it.userData, it.isLoggedIn) { mutableStateOf(isAllowedPermission()) }

                PixiViewTheme(
                    fanboxCookie = it.fanboxCookie,
                    fanboxMetadata = it.fanboxMetadata,
                    themeColorConfig = it.userData.themeColorConfig,
                    shouldUseDarkTheme = shouldUseDarkTheme,
                    enableDynamicTheme = shouldUseDynamicColor(screenState),
                ) {
                    Box {
                        PixiViewApp(
                            modifier = Modifier.fillMaxSize(),
                            viewModel = viewModel,
                            windowSize = windowSize.widthSizeClass,
                            userData = it.userData,
                            isAgreedTeams = isAgreedTeams,
                            isAllowedPermission = isAllowedPermission,
                            isLoggedIn = it.isLoggedIn,
                            onCompleteWelcome = {
                                isAgreedTeams = true
                                isAllowedPermission = true

                                viewModel.updateState()
                            },
                        )

                        AnimatedVisibility(
                            visible = it.isAppLocked,
                            enter = fadeIn(),
                            exit = fadeOut(),
                        ) {
                            LoadingView(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface),
                            )
                        }

                        if (it.isAppLocked) {
                            LaunchedEffect(true) {
                                requireAuth()
                            }
                        }

                        LaunchedEffect(it.userData.isAppLock) {
                            if (!it.userData.isAppLock) {
                                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                                viewModel.setAppLock(false)
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                                setRecentsScreenshotEnabled(!it.userData.isAppLock)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        Intent(this, PostDownloadService::class.java).also { intent ->
            bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setAppLock(true)
    }

    override fun onStop() {
        super.onStop()

        unbindService(serviceConnection)
        isPostDownloadServiceBound = false
    }

    override fun onDownloadImages(imageItems: List<FanboxPostDetail.ImageItem>) {
        if (isPostDownloadServiceBound) {
            startForegroundService(Intent(this, PostDownloadService::class.java))
            postDownloadService.downloadImages(imageItems)
        } else {
            ToastUtil.show(this, R.string.error_unknown)
        }
    }

    override fun onDownloadFile(fileItem: FanboxPostDetail.FileItem) {
        if (isPostDownloadServiceBound) {
            startForegroundService(Intent(this, PostDownloadService::class.java))
            postDownloadService.downloadFile(fileItem)
        } else {
            ToastUtil.show(this, R.string.error_unknown)
        }
    }

    override fun onDownloadPosts(posts: List<FanboxPost>, isIgnoreFree: Boolean, isIgnoreFile: Boolean) {
        if (isPostDownloadServiceBound) {
            startForegroundService(Intent(this, PostDownloadService::class.java))
            postDownloadService.downloadPosts(posts, isIgnoreFree, isIgnoreFile)
        } else {
            ToastUtil.show(this, R.string.error_unknown)
        }
    }

    private suspend fun requireAuth() {
        if (biometricDialog.auth()) {
            viewModel.setAppLock(false)
        } else {
            finishAffinity()
            killProcess(android.os.Process.myPid())
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
