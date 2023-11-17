package caios.android.pixiview.feature.setting.top.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.feature.setting.R
import caios.android.pixiview.feature.setting.SettingSwitchItem

@Composable
internal fun SettingTopGeneralSection(
    userData: UserData,
    onClickFollowTabDefaultHome: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        SettingTopTitleItem(
            modifier = Modifier.fillMaxWidth(),
            text = R.string.setting_top_others,
        )

        SettingSwitchItem(
            modifier = Modifier.fillMaxWidth(),
            title = R.string.setting_top_general_default_follow_tab,
            description = R.string.setting_top_general_default_follow_tab_description,
            value = userData.isFollowTabDefaultHome,
            onValueChanged = onClickFollowTabDefaultHome,
        )
    }
}
