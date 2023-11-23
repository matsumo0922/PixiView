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
import caios.android.pixiview.feature.setting.SettingTextItem

@Composable
internal fun SettingTopInformationSection(
    userData: UserData,
    fanboxMetaData: FanboxMetaData,
    fanboxSessionId: String,
    config: PixiViewConfig,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboardManager.current

    Column(modifier) {
        SettingTopTitleItem(
            modifier = Modifier.fillMaxWidth(),
            text = R.string.setting_top_information,
        )

        SettingTextItem(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.setting_top_information_version),
            description = "${config.versionName}:${config.versionCode}" + when {
                userData.isPlusMode && userData.isDeveloperMode -> " [P+D]"
                userData.isPlusMode -> " [Premium]"
                userData.isDeveloperMode -> " [Developer]"
                else -> ""
            },
            onClick = { /* do nothing */ },
        )

        SettingTextItem(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.setting_top_information_id),
            description = userData.pixiViewId,
            onLongClick = { clipboard.setText(AnnotatedString(userData.pixiViewId)) },
        )

        SettingTextItem(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.setting_top_information_fanbox_session_id),
            description = fanboxSessionId,
            onLongClick = { clipboard.setText(AnnotatedString(fanboxSessionId)) },
        )

        SettingTextItem(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.setting_top_information_csrf_token),
            description = fanboxMetaData.csrfToken,
            onLongClick = { clipboard.setText(AnnotatedString(fanboxMetaData.csrfToken)) },
        )
    }
}
