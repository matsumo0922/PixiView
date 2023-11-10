package caios.android.pixiview.feature.post.detail.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.theme.bold
import caios.android.pixiview.core.ui.theme.center
import caios.android.pixiview.core.ui.theme.end
import caios.android.pixiview.core.ui.theme.start
import caios.android.pixiview.feature.post.R
import java.time.format.DateTimeFormatter

@Composable
internal fun PostDetailOtherPostSection(
    nextPost: FanboxPostDetail.OtherPost?,
    previousPost: FanboxPostDetail.OtherPost?,
    onClickNextPost: (PostId) -> Unit,
    onClickPreviousPost: (PostId) -> Unit,
    onClickAllPosts: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (previousPost != null) {
                PreviousPostButton(
                    modifier = Modifier.weight(1f),
                    previousPost = previousPost,
                    onClickPreviousPost = onClickPreviousPost,
                )
            } else {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    text = stringResource(R.string.post_detail_oldest_post),
                    style = MaterialTheme.typography.bodyMedium.center(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (nextPost != null) {
                NextPostButton(
                    modifier = Modifier.weight(1f),
                    nextPost = nextPost,
                    onClickNextPost = onClickNextPost,
                )
            } else {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    text = stringResource(R.string.post_detail_newest_post),
                    style = MaterialTheme.typography.bodyMedium.center(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClickAllPosts.invoke() }
                .padding(16.dp),
            text = stringResource(R.string.post_detail_all_posts),
            style = MaterialTheme.typography.bodyMedium.center(),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun NextPostButton(
    nextPost: FanboxPostDetail.OtherPost,
    onClickNextPost: (PostId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable { onClickNextPost(nextPost.id) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically,
            ),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = nextPost.title,
                style = MaterialTheme.typography.bodyMedium.bold().end(),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = nextPost.publishedDatetime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                style = MaterialTheme.typography.bodySmall.end(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
        )
    }
}

@Composable
private fun PreviousPostButton(
    previousPost: FanboxPostDetail.OtherPost,
    onClickPreviousPost: (PostId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable { onClickPreviousPost(previousPost.id) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
            contentDescription = null,
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically,
            ),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = previousPost.title,
                style = MaterialTheme.typography.bodyMedium.bold().start(),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = previousPost.publishedDatetime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                style = MaterialTheme.typography.bodySmall.start(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
