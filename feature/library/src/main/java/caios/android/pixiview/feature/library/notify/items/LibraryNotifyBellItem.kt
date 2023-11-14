package caios.android.pixiview.feature.library.notify.items

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.model.fanbox.FanboxBell
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.extensition.SimmerPlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import caios.android.pixiview.feature.library.R
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import java.time.Duration
import java.time.OffsetDateTime

@Composable
internal fun LibraryNotifyBellItem(
    bell: FanboxBell,
    onClickBell: (PostId) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (bell) {
        is FanboxBell.Comment -> {
            CommentItem(
                modifier = modifier,
                bell = bell,
                onClickBell = onClickBell,
            )
        }
        is FanboxBell.Like -> {
            LikeItem(
                modifier = modifier,
                bell = bell,
                onClickBell = onClickBell,
            )
        }
        is FanboxBell.PostPublished -> {
            PostPublishedItem(
                modifier = modifier,
                bell = bell,
                onClickBell = onClickBell,
            )
        }
    }
}

@Composable
private fun PostPublishedItem(
    bell: FanboxBell.PostPublished,
    onClickBell: (PostId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isShowCard = (bell.post.cover != null && bell.post.excerpt.isNotBlank())

    Row(
        modifier = modifier
            .clickable { onClickBell.invoke(bell.post.id) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = if (isShowCard) Alignment.Top else Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .error(R.drawable.im_default_user)
                .data(bell.post.user.iconUrl)
                .build(),
            contentDescription = null,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.notify_title_post_published, bell.post.title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = bell.post.publishedDatetime.toRelativeTimeString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (isShowCard) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(4.dp),
                        )
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        modifier = Modifier.weight(5f),
                        text = bell.post.excerpt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                    )

                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .weight(3f)
                            .aspectRatio(16 / 9f)
                            .clip(RoundedCornerShape(4.dp)),
                        model = ImageRequest.Builder(LocalContext.current)
                            .fanboxHeader()
                            .data(bell.post.cover?.url)
                            .build(),
                        loading = {
                            SimmerPlaceHolder()
                        },
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentItem(
    bell: FanboxBell.Comment,
    onClickBell: (PostId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable { onClickBell.invoke(bell.postId) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AsyncImage(
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .error(R.drawable.im_default_user)
                .data(bell.userProfileIconUrl)
                .build(),
            contentDescription = null,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.notify_title_comment, bell.userName),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = bell.notifiedDatetime.toRelativeTimeString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = bell.comment,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LikeItem(
    bell: FanboxBell.Like,
    onClickBell: (PostId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable { onClickBell.invoke(bell.postId) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp),
            imageVector = Icons.Default.Favorite,
            tint = Color.Red,
            contentDescription = null,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.notify_title_like, bell.count),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = bell.notifiedDatetime.toRelativeTimeString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = bell.comment,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun OffsetDateTime.toRelativeTimeString(): String {
    val now = OffsetDateTime.now()
    val duration = Duration.between(this, now)

    return when {
        duration.toDays() > 0 -> stringResource(R.string.unit_day_before, duration.toDays())
        duration.toHours() > 0 -> stringResource(R.string.unit_hour_before, duration.toHours())
        duration.toMinutes() > 0 -> stringResource(R.string.unit_minute_before, duration.toMinutes())
        else -> stringResource(R.string.unit_second_before, duration.seconds)
    }
}
