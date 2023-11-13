package caios.android.pixiview.feature.library.message.items

import android.os.Build
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.widget.TextView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.util.LinkifyCompat
import caios.android.pixiview.core.model.fanbox.FanboxNewsLetter
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.ui.component.AutoLinkText
import caios.android.pixiview.core.ui.theme.bold
import caios.android.pixiview.feature.library.R
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.time.format.DateTimeFormatter

@Composable
internal fun LibraryMessageItem(
    message: FanboxNewsLetter,
    onClickCreator: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowBigBody by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .animateContentSize()
            .clickable { isShowBigBody = true }
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { message.creator.creatorId?.let { onClickCreator.invoke(it) } }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(36.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .crossfade(true)
                        .error(R.drawable.im_default_user)
                        .data(message.creator.user.iconUrl)
                        .build(),
                    contentDescription = null,
                )

                Text(
                    modifier = Modifier.weight(1f),
                    text = message.creator.user.name,
                    style = MaterialTheme.typography.bodyMedium.bold(),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Text(
                modifier = Modifier.padding(end = 8.dp),
                text = message.createdAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (isShowBigBody) {
            AutoLinkText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                text = message.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        } else {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                text = message.body.replace("\n", " "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
