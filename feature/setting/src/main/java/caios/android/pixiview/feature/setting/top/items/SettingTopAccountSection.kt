package caios.android.pixiview.feature.setting.top.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import caios.android.pixiview.feature.setting.R
import caios.android.pixiview.feature.setting.SettingTextItem

@Composable
internal fun SettingTopAccountSection(
    onClickAccountSetting: () -> Unit,
    onClickNotifySetting: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        SettingTopTitleItem(
            modifier = Modifier.fillMaxWidth(),
            text = R.string.setting_account,
        )

        SettingTextItem(
            modifier = Modifier.fillMaxWidth(),
            title = R.string.setting_account_title,
            description = R.string.setting_account_description,
            onClick = onClickAccountSetting,
        )

        SettingTextItem(
            modifier = Modifier.fillMaxWidth(),
            title = R.string.setting_notify_title,
            description = R.string.setting_notify_description,
            onClick = onClickNotifySetting,
        )
    }
}
