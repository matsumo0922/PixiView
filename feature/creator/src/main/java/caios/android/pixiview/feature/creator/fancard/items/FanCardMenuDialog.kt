package caios.android.pixiview.feature.creator.fancard.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import caios.android.pixiview.feature.creator.R

@Composable
internal fun FanCardMenuDialog(
    onClickSwitchDisplayName: () -> Unit,
    onClickDownload: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 8.dp),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClickSwitchDisplayName.invoke()
                        onDismissRequest.invoke()
                    }
                    .padding(20.dp, 16.dp),
                text = stringResource(R.string.creator_fan_card_switch_displa_name),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClickDownload.invoke()
                        onDismissRequest.invoke()
                    }
                    .padding(20.dp, 16.dp),
                text = stringResource(R.string.creator_fan_card_download),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
