package caios.android.pixiview.feature.post.detail.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.ui.theme.bold
import caios.android.pixiview.feature.post.R
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.time.Duration
import java.time.OffsetDateTime

@Composable
internal fun PostDetailCommentSection(
    post: FanboxPostDetail,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.post_detail_comment_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            for (comment in post.commentList.items) {
                CommentItem(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    comment = comment,
                )
            }
        }
    }
}

@Composable
private fun CommentItem(
    comment: FanboxPostDetail.Comment.CommentItem,
    modifier: Modifier = Modifier,
) {
    val createdDateBefore = remember { (Duration.between(comment.createdDatetime, OffsetDateTime.now()).toDays()) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AsyncImage(
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .error(R.drawable.im_default_user)
                .data(comment.user.iconUrl)
                .build(),
            contentDescription = null,
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = comment.user.name,
                style = MaterialTheme.typography.bodyMedium.bold(),
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = comment.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.unit_day_before, createdDateBefore),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = stringResource(R.string.unit_favorite, comment.likeCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
