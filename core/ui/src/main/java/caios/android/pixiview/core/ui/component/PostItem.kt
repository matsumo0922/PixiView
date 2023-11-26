package caios.android.pixiview.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoAdultContent
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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
import caios.android.pixiview.core.ui.extensition.FadePlaceHolder
import caios.android.pixiview.core.ui.extensition.SimmerPlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import caios.android.pixiview.core.ui.theme.bold
import caios.android.pixiview.core.ui.theme.center
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import java.time.format.DateTimeFormatter

@Composable
fun PostItem(
    post: FanboxPost,
    onClickPost: (PostId) -> Unit,
    onClickCreator: (CreatorId) -> Unit,
    onClickPlanList: (CreatorId) -> Unit,
    onClickLike: (PostId) -> Unit,
    onClickBookmark: (PostId, Boolean) -> Unit,
    isHideAdultContents: Boolean,
    modifier: Modifier = Modifier,
) {
    var isPostBookmarked by remember(post.isBookmarked) { mutableStateOf(post.isBookmarked) }
    var isPostLiked by rememberSaveable(post.isLiked) { mutableStateOf(post.isLiked) }
    var isHideAdultContent by remember { mutableStateOf(isHideAdultContents) }

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClickPost.invoke(post.id) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            when {
                post.isRestricted -> {
                    RestrictThumbnail(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9),
                        feeRequired = post.feeRequired,
                    )
                }

                post.cover == null -> {
                    FileThumbnail(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9),
                    )
                }

                post.hasAdultContent && isHideAdultContent -> {
                    AdultContentThumbnail(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9),
                        coverImageUrl = post.cover?.url,
                        onClickShowAdultContent = { isHideAdultContent = false },
                    )
                }

                else -> {
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9),
                        model = ImageRequest.Builder(LocalContext.current)
                            .fanboxHeader()
                            .data(post.cover?.url)
                            .build(),
                        loading = {
                            SimmerPlaceHolder()
                        },
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                    )
                }
            }

            UserSection(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                post = post,
                onClickCreator = onClickCreator,
            )

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                text = post.title,
                style = MaterialTheme.typography.titleMedium.bold(),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            if (post.isRestricted) {
                RestrictCardItem(
                    modifier = Modifier
                        .padding(
                            top = 8.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp,
                        )
                        .fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    feeRequired = post.feeRequired,
                    onClickPlanList = { onClickPlanList.invoke(post.user.creatorId) },
                )
            } else {
                if (post.excerpt.isNotBlank()) {
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
                }

                CommentLikeButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 8.dp,
                            bottom = 8.dp,
                        ),
                    commentCount = post.commentCount,
                    likeCount = post.likeCount + if (isPostLiked) 1 else 0,
                    isBookmarked = isPostBookmarked,
                    isLiked = isPostLiked,
                    onClickLike = {
                        isPostLiked = true
                        onClickLike.invoke(post.id)
                    },
                    onClickBookmark = {
                        isPostBookmarked = it
                        onClickBookmark.invoke(post.id, it)
                    },
                )
            }
        }
    }
}

@Composable
private fun UserSection(
    post: FanboxPost,
    onClickCreator: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(4.dp))
                .clickable { onClickCreator.invoke(post.user.creatorId) }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                model = ImageRequest.Builder(LocalContext.current)
                    .error(R.drawable.im_default_user)
                    .data(post.user.iconUrl)
                    .build(),
                loading = {
                    FadePlaceHolder()
                },
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
        }

        Card(
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Text(
                modifier = Modifier.padding(6.dp, 4.dp),
                text = if (post.feeRequired == 0) stringResource(R.string.fanbox_free_fee) else stringResource(R.string.unit_jpy, post.feeRequired),
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
private fun AdultContentThumbnail(
    coverImageUrl: String?,
    onClickShowAdultContent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .fanboxHeader()
                .data(coverImageUrl)
                .build(),
            loading = {
                SimmerPlaceHolder()
            },
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.CenterVertically,
            ),
        ) {
            Icon(
                modifier = Modifier.size(48.dp),
                imageVector = Icons.Default.NoAdultContent,
                tint = Color.White,
                contentDescription = null,
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.error_adult_content),
                style = MaterialTheme.typography.bodySmall.center(),
                color = Color.White,
            )

            Button(onClick = { onClickShowAdultContent.invoke() }) {
                Text(text = stringResource(R.string.error_adult_content_description))
            }
        }
    }
}

@Composable
private fun FileThumbnail(
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
            imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
            tint = Color.White,
            contentDescription = null,
        )
    }
}

@Composable
private fun CommentLikeButton(
    commentCount: Int,
    likeCount: Int,
    isBookmarked: Boolean,
    isLiked: Boolean,
    onClickLike: () -> Unit,
    onClickBookmark: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val likeColor = if (isLiked) Color(0xffe0405e) else MaterialTheme.colorScheme.onSurfaceVariant
    val bookmarkColor = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
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
                .clip(CircleShape)
                .clickable {
                    if (!isLiked) {
                        onClickLike.invoke()
                    }
                    onClickBookmark.invoke(true)
                }
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
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

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { onClickBookmark.invoke(!isBookmarked) },
        ) {
            Icon(
                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                tint = bookmarkColor,
                contentDescription = null,
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
        onClickCreator = {},
        onClickLike = {},
        onClickBookmark = { _, _ -> },
        isHideAdultContents = false,
    )
}

@Preview
@Composable
private fun PostItemPreview2() {
    PostItem(
        post = FanboxPost.dummy().copy(isRestricted = true),
        onClickPost = {},
        onClickPlanList = {},
        onClickCreator = {},
        onClickLike = {},
        onClickBookmark = { _, _ -> },
        isHideAdultContents = false,
    )
}

@Preview
@Composable
private fun PostItemPreview3() {
    PostItem(
        post = FanboxPost.dummy().copy(hasAdultContent = true),
        onClickPost = {},
        onClickPlanList = {},
        onClickCreator = {},
        onClickLike = {},
        onClickBookmark = { _, _ -> },
        isHideAdultContents = true,
    )
}
