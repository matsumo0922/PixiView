package caios.android.pixiview

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import caios.android.pixiview.core.common.util.ToastUtil
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.ThemeConfig
import caios.android.pixiview.core.model.contract.PostDownloader
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.theme.DarkDefaultColorScheme
import caios.android.pixiview.core.ui.theme.LightDefaultColorScheme
import caios.android.pixiview.core.ui.theme.PixiViewTheme
import caios.android.pixiview.core.ui.view.BiometricDialog
import caios.android.pixiview.core.ui.view.LoadingView
import caios.android.pixiview.feature.post.service.PostDownloadService
import caios.android.pixiview.ui.PixiViewApp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    postDownloadService.downloadedEvent.collect { _ ->
                        ToastUtil.show(this@MainActivity, R.string.common_downloaded)
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isPostDownloadServiceBound = false
        }
    }

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
                val isAgreedTeams =
                    remember(it.userData) { it.userData.isAgreedPrivacyPolicy && it.userData.isAgreedTermsOfService }
                val isAllowedPermission = remember(it.userData, it.isLoggedIn) { isAllowedPermission() }

                PixiViewTheme(
                    fanboxCookie = it.fanboxCookie,
                    themeColorConfig = it.userData.themeColorConfig,
                    shouldUseDarkTheme = shouldUseDarkTheme,
                    enableDynamicTheme = shouldUseDynamicColor(screenState),
                ) {
                    Box {
                        PixiViewApp(
                            modifier = Modifier.fillMaxSize(),
                            viewModel = viewModel,
                            userData = it.userData,
                            isAgreedTeams = isAgreedTeams,
                            isAllowedPermission = isAllowedPermission,
                            isLoggedIn = it.isLoggedIn,
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
