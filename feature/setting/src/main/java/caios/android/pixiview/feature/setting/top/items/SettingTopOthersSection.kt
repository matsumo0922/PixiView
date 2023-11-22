package caios.android.pixiview.feature.setting.top.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import caios.android.pixiview.core.common.PixiViewConfig
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.model.fanbox.FanboxMetaData
import caios.android.pixiview.feature.setting.R
import caios.android.pixiview.feature.setting.SettingSwitchItem
import caios.android.pixiview.feature.setting.SettingTextItem

@Composable
internal fun SettingTopOthersSection(
    userData: UserData,
    onClickLogout: () -> Unit,
    onClickOpenSourceLicense: () -> Unit,
    onClickDeveloperMode: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        SettingTopTitleItem(
            modifier = Modifier.fillMaxWidth(),
            text = R.string.setting_top_others,
        )

        SettingTextItem(
            modifier = Modifier.fillMaxWidth(),
            title = R.string.setting_top_others_logout,
            description = R.string.setting_top_others_logout_description,
            onClick = onClickLogout,
        )

        SettingTextItem(
            modifier = Modifier.fillMaxWidth(),
            title = R.string.setting_top_others_open_source_license,
            description = R.string.setting_top_others_open_source_license_description,
            onClick = { onClickOpenSourceLicense.invoke() },
        )

        SettingSwitchItem(
            modifier = Modifier.fillMaxWidth(),
            title = R.string.setting_top_others_developer_mode,
            description = R.string.setting_top_others_developer_mode_description,
            value = userData.isDeveloperMode,
            onValueChanged = onClickDeveloperMode,
        )
    }
}
