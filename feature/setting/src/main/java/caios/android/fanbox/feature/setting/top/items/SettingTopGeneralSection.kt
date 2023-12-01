package caios.android.fanbox.feature.setting.top.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import caios.android.fanbox.core.model.UserData
import caios.android.fanbox.feature.setting.R
import caios.android.fanbox.feature.setting.SettingSwitchItem

@Composable
internal fun SettingTopGeneralSection(
    userData: UserData,
    onClickAppLock: (Boolean) -> Unit,
    onClickFollowTabDefaultHome: (Boolean) -> Unit,
    onClickHideAdultContents: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        SettingTopTitleItem(
            modifier = Modifier.fillMaxWidth(),
            text = R.string.setting_top_general,
        )

        SettingSwitchItem(
            modifier = Modifier.fillMaxWidth(),
            title = R.string.setting_top_general_app_lock,
            description = R.string.setting_top_general_app_lock_description,
            value = userData.isAppLock,
            onValueChanged = onClickAppLock,
        )

        SettingSwitchItem(
            modifier = Modifier.fillMaxWidth(),
            title = R.string.setting_top_general_default_follow_tab,
            description = R.string.setting_top_general_default_follow_tab_description,
            value = userData.isFollowTabDefaultHome,
            onValueChanged = onClickFollowTabDefaultHome,
        )

        SettingSwitchItem(
            modifier = Modifier.fillMaxWidth(),
            title = R.string.setting_top_general_hide_adult_contents,
            description = R.string.setting_top_general_hide_adult_contents_description,
            value = userData.isHideAdultContents,
            onValueChanged = onClickHideAdultContents,
        )
    }
}
