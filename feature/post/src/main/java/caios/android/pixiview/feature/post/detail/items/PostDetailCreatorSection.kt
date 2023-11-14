package caios.android.pixiview.feature.post.detail.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.ui.theme.bold
import caios.android.pixiview.feature.post.R
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
internal fun PostDetailCreatorSection(
    post: FanboxPostDetail,
    onClickCreator: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                )
                .fillMaxWidth(),
            text = stringResource(R.string.post_detail_creator),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        CreatorItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClickCreator.invoke(post.user.creatorId) }
                .padding(
                    top = 16.dp,
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 16.dp,
                ),
            post = post,
        )
    }
}

@Composable
private fun CreatorItem(
    post: FanboxPostDetail,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AsyncImage(
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .error(R.drawable.im_default_user)
                .data(post.user.iconUrl)
                .build(),
            contentDescription = null,
        )

        Text(
            modifier = Modifier.weight(1f),
            text = post.user.name,
            style = MaterialTheme.typography.bodyLarge.bold(),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
        )
    }
}
