package caios.android.pixiview.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.R
import caios.android.pixiview.core.ui.theme.bold
import caios.android.pixiview.core.ui.theme.center
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.time.format.DateTimeFormatter

@Composable
fun PostItem(
    post: FanboxPost,
    onClickPost: (PostId) -> Unit,
    onClickPlanList: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClickPost.invoke(post.id) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (post.isRestricted) {
                RestrictThumbnail(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9),
                    feeRequired = post.feeRequired,
                )
            } else {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.cover?.url)
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
            }

            UserSection(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                post = post,
            )

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                text = post.title,
                style = MaterialTheme.typography.titleMedium.bold(),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            if (post.isRestricted) {
                RestrictCard(
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp,
                        )
                        .fillMaxWidth(),
                    feeRequired = post.feeRequired,
                    onClickPlanList = { onClickPlanList.invoke(post.user.creatorId) },
                )
            } else {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    text = post.excerpt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                CommentLikeButton(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp,
                        ),
                    commentCount = post.commentCount,
                    likeCount = post.likeCount,
                    isLiked = post.isLiked,
                )
            }
        }
    }
}

@Composable
private fun UserSection(
    post: FanboxPost,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(50)),
            model = ImageRequest.Builder(LocalContext.current)
                .data(post.user.iconUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = post.user.name,
                style = MaterialTheme.typography.bodyMedium.bold(),
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = post.publishedDatetime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Card(
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Text(
                modifier = Modifier.padding(6.dp, 4.dp),
                text = if (post.feeRequired == 0) stringResource(R.string.fanbox_free_fee) else "￥${post.feeRequired}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RestrictThumbnail(
    feeRequired: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color.DarkGray)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically,
        ),
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            imageVector = Icons.Default.Lock,
            tint = Color.White,
            contentDescription = null,
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.error_restricted_post, feeRequired),
            style = MaterialTheme.typography.bodySmall.center(),
            color = Color.White,
        )
    }
}

@Composable
private fun RestrictCard(
    feeRequired: Int,
    onClickPlanList: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.Lock,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = null,
            )

            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.error_restricted_post, feeRequired),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Button(onClick = { onClickPlanList.invoke() }) {
                Text(stringResource(R.string.fanbox_plan_List))
            }
        }
    }
}

@Composable
private fun CommentLikeButton(
    commentCount: Int,
    likeCount: Int,
    isLiked: Boolean,
    modifier: Modifier = Modifier,
) {
    val likeColor = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RoundedCornerShape(50),
                )
                .padding(8.dp, 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.AutoMirrored.Filled.Comment,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = null,
            )

            Text(
                text = commentCount.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = likeColor,
                    shape = RoundedCornerShape(50),
                )
                .padding(8.dp, 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Default.Favorite,
                tint = likeColor,
                contentDescription = null,
            )

            Text(
                text = likeCount.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = likeColor,
            )
        }
    }
}

@Preview
@Composable
private fun PostItemPreview1() {
    PostItem(
        post = FanboxPost.dummy(),
        onClickPost = {},
        onClickPlanList = {},
    )
}

@Preview
@Composable
private fun PostItemPreview2() {
    PostItem(
        post = FanboxPost.dummy().copy(isRestricted = true),
        onClickPost = {},
        onClickPlanList = {},
    )
}
