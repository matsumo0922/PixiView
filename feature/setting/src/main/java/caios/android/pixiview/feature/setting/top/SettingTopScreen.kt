package caios.android.pixiview.feature.setting.top

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.pixiview.core.common.PixiViewConfig
import caios.android.pixiview.core.common.util.ToastUtil
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.model.fanbox.FanboxMetaData
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.view.SimpleAlertContents
import caios.android.pixiview.feature.setting.R
import caios.android.pixiview.feature.setting.SettingTheme
import caios.android.pixiview.feature.setting.top.items.SettingTopGeneralSection
import caios.android.pixiview.feature.setting.top.items.SettingTopInformationSection
import caios.android.pixiview.feature.setting.top.items.SettingTopOthersSection
import caios.android.pixiview.feature.setting.top.items.SettingTopThemeSection
import kotlinx.coroutines.launch

@Composable
internal fun SettingTopRoute(
    navigateToSettingTheme: () -> Unit,
    navigateToLogoutDialog: (SimpleAlertContents, () -> Unit) -> Unit,
    navigateToOpenSourceLicense: () -> Unit,
    navigateToSettingDeveloper: () -> Unit,
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingTopViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
    ) { uiState ->
        SettingTopScreen(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            userData = uiState.userData,
            metaData = uiState.metaData,
            fanboxSessionId = uiState.fanboxSessionId,
            config = uiState.config,
            onClickTheme = navigateToSettingTheme,
            onClickOpenSourceLicense = navigateToOpenSourceLicense,
            onClickAppLock = viewModel::setAppLock,
            onClickFollowTabDefaultHome = viewModel::setFollowTabDefaultHome,
            onClickHideAdultContents = viewModel::setHideAdultContents,
            onClickLogout = {
                navigateToLogoutDialog(SimpleAlertContents.Logout) {
                    scope.launch {
                        viewModel.logout().fold(
                            onSuccess = {
                                ToastUtil.show(context, R.string.setting_top_others_logout_dialog_success)
                                terminate.invoke()
                            },
                            onFailure = {
                                ToastUtil.show(context, R.string.setting_top_others_logout_dialog_failed)
                            },
                        )
                    }
                }
            },
            onClickDeveloperMode = { isEnable ->
                if (isEnable) {
                    navigateToSettingDeveloper.invoke()
                } else {
                    viewModel.setDeveloperMode(false)
                }
            },
            onTerminate = terminate,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingTopScreen(
    userData: UserData,
    metaData: FanboxMetaData,
    fanboxSessionId: String,
    config: PixiViewConfig,
    onClickTheme: () -> Unit,
    onClickAppLock: (Boolean) -> Unit,
    onClickFollowTabDefaultHome: (Boolean) -> Unit,
    onClickHideAdultContents: (Boolean) -> Unit,
    onClickLogout: () -> Unit,
    onClickOpenSourceLicense: () -> Unit,
    onClickDeveloperMode: (Boolean) -> Unit,
    onTerminate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberTopAppBarState()
    val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state)

    Scaffold(
        modifier = modifier.nestedScroll(behavior.nestedScrollConnection),
        topBar = {
            SettingTheme {
                LargeTopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = {
                        Text(
                            text = stringResource(R.string.setting_title),
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onTerminate) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    },
                    scrollBehavior = behavior,
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
        ) {
            item {
                SettingTopThemeSection(
                    modifier = Modifier.fillMaxWidth(),
                    onClickAppTheme = onClickTheme,
                )

                SettingTopGeneralSection(
                    modifier = Modifier.fillMaxWidth(),
                    userData = userData,
                    onClickAppLock = onClickAppLock,
                    onClickFollowTabDefaultHome = onClickFollowTabDefaultHome,
                    onClickHideAdultContents = onClickHideAdultContents,
                )

                SettingTopInformationSection(
                    modifier = Modifier.fillMaxWidth(),
                    config = config,
                    userData = userData,
                    fanboxMetaData = metaData,
                    fanboxSessionId = fanboxSessionId,
                )

                SettingTopOthersSection(
                    modifier = Modifier.fillMaxWidth(),
                    userData = userData,
                    onClickLogout = onClickLogout,
                    onClickOpenSourceLicense = onClickOpenSourceLicense,
                    onClickDeveloperMode = onClickDeveloperMode,
                )
            }
        }
    }
}
