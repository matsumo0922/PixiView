package caios.android.fanbox.feature.setting.top

import android.content.Intent
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.fanbox.core.common.PixiViewConfig
import caios.android.fanbox.core.common.util.ToastUtil
import caios.android.fanbox.core.model.UserData
import caios.android.fanbox.core.model.fanbox.FanboxMetaData
import caios.android.fanbox.core.ui.AsyncLoadContents
import caios.android.fanbox.core.ui.view.BiometricDialog
import caios.android.fanbox.core.ui.view.SimpleAlertContents
import caios.android.fanbox.feature.setting.R
import caios.android.fanbox.feature.setting.SettingTheme
import caios.android.fanbox.feature.setting.top.items.SettingTopAccountSection
import caios.android.fanbox.feature.setting.top.items.SettingTopGeneralSection
import caios.android.fanbox.feature.setting.top.items.SettingTopInformationSection
import caios.android.fanbox.feature.setting.top.items.SettingTopOthersSection
import caios.android.fanbox.feature.setting.top.items.SettingTopThemeSection
import kotlinx.coroutines.launch

@Composable
internal fun SettingTopRoute(
    navigateToThemeSetting: () -> Unit,
    navigateToBillingPlus: () -> Unit,
    navigateToLogoutDialog: (SimpleAlertContents, () -> Unit) -> Unit,
    navigateToOpenSourceLicense: () -> Unit,
    navigateToSettingDeveloper: () -> Unit,
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingTopViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val biometricDialog = remember { BiometricDialog(context as FragmentActivity) }
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
            onClickThemeSetting = navigateToThemeSetting,
            onClickAccountSetting = {
                context.startActivity(Intent(Intent.ACTION_VIEW, "https://www.fanbox.cc/user/settings".toUri()))
            },
            onClickNotifySetting = {
                context.startActivity(Intent(Intent.ACTION_VIEW, "https://www.fanbox.cc/notifications/settings#email".toUri()))
            },
            onClickTeamsOfService = {
                context.startActivity(Intent(Intent.ACTION_VIEW, "https://www.matsumo.me/application/pixiview/team_of_service".toUri()))
            },
            onClickPrivacyPolicy = {
                context.startActivity(Intent(Intent.ACTION_VIEW, "https://www.matsumo.me/application/pixiview/privacy_policy".toUri()))
            },
            onClickOpenSourceLicense = navigateToOpenSourceLicense,
            onClickFollowTabDefaultHome = viewModel::setFollowTabDefaultHome,
            onClickHideAdultContents = viewModel::setHideAdultContents,
            onClickOverrideAdultContents = viewModel::setOverrideAdultContents,
            onClickGridMode =  {
                if (it) {
                    if (uiState.userData.hasPrivilege) {
                        viewModel.setGridMode(true)
                    } else {
                        ToastUtil.show(context, R.string.billing_plus_toast_require_plus)
                        navigateToBillingPlus.invoke()
                    }
                } else {
                    viewModel.setGridMode(false)
                }
            },
            onClickHideRestricted = {
                if (it) {
                    if (uiState.userData.hasPrivilege) {
                        viewModel.setHideRestricted(true)
                    } else {
                        ToastUtil.show(context, R.string.billing_plus_toast_require_plus)
                        navigateToBillingPlus.invoke()
                    }
                } else {
                    viewModel.setHideRestricted(false)
                }
            },
            onClickAppLock = {
                if (it) {
                    if (uiState.userData.hasPrivilege) {
                        scope.launch {
                            if (biometricDialog.auth()) {
                                viewModel.setAppLock(true)
                            }
                        }
                    } else {
                        ToastUtil.show(context, R.string.billing_plus_toast_require_plus)
                        navigateToBillingPlus.invoke()
                    }
                } else {
                    viewModel.setAppLock(false)
                }
            },
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
    onClickThemeSetting: () -> Unit,
    onClickAccountSetting: () -> Unit,
    onClickNotifySetting: () -> Unit,
    onClickAppLock: (Boolean) -> Unit,
    onClickFollowTabDefaultHome: (Boolean) -> Unit,
    onClickHideAdultContents: (Boolean) -> Unit,
    onClickOverrideAdultContents: (Boolean) -> Unit,
    onClickHideRestricted: (Boolean) -> Unit,
    onClickGridMode: (Boolean) -> Unit,
    onClickTeamsOfService: () -> Unit,
    onClickPrivacyPolicy: () -> Unit,
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
                SettingTopAccountSection(
                    modifier = Modifier.fillMaxWidth(),
                    onClickAccountSetting = onClickAccountSetting,
                    onClickNotifySetting = onClickNotifySetting,
                )

                SettingTopThemeSection(
                    modifier = Modifier.fillMaxWidth(),
                    onClickAppTheme = onClickThemeSetting,
                )

                SettingTopGeneralSection(
                    modifier = Modifier.fillMaxWidth(),
                    userData = userData,
                    onClickAppLock = onClickAppLock,
                    onClickFollowTabDefaultHome = onClickFollowTabDefaultHome,
                    onClickHideAdultContents = onClickHideAdultContents,
                    onClickOverrideAdultContents = onClickOverrideAdultContents,
                    onClickHideRestricted = onClickHideRestricted,
                    onClickGridMode = onClickGridMode,
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
                    onClickTeamsOfService = onClickTeamsOfService,
                    onClickPrivacyPolicy = onClickPrivacyPolicy,
                    onClickLogout = onClickLogout,
                    onClickOpenSourceLicense = onClickOpenSourceLicense,
                    onClickDeveloperMode = onClickDeveloperMode,
                )
            }
        }
    }
}
