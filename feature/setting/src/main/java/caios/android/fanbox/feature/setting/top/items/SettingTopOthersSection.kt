package caios.android.fanbox.feature.setting.top.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import caios.android.fanbox.core.model.UserData
import caios.android.fanbox.feature.setting.R
import caios.android.fanbox.feature.setting.SettingSwitchItem
import caios.android.fanbox.feature.setting.SettingTextItem

@Composable
internal fun SettingTopOthersSection(
    userData: UserData,
    onClickTeamsOfService: () -> Unit,
    onClickPrivacyPolicy: () -> Unit,
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
            title = stringResource(R.string.setting_top_information_team_of_service),
            onClick = onClickTeamsOfService,
        )

        SettingTextItem(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.setting_top_information_privacy_policy),
            onClick = onClickPrivacyPolicy,
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
