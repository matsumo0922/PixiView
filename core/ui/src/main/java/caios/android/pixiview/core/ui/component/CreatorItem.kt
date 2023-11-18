package caios.android.pixiview.core.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.ui.R
import caios.android.pixiview.core.ui.extensition.FadePlaceHolder
import caios.android.pixiview.core.ui.extensition.SimmerPlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import caios.android.pixiview.core.ui.theme.bold
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreatorItem(
    creatorDetail: FanboxCreatorDetail,
    onClickCreator: (CreatorId) -> Unit,
    onClickFollow: (String) -> Unit,
    onClickUnfollow: (String) -> Unit,
    modifier: Modifier = Modifier,
    isFollowed: Boolean = creatorDetail.isFollowed,
) {
    var isEllipsized by remember { mutableStateOf(false) }
    var isDisplayedAll by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClickCreator.invoke(creatorDetail.creatorId) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)),
    ) {
        if (creatorDetail.coverImageUrl != null) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(4.dp)),
                model = ImageRequest.Builder(LocalContext.current)
                    .fanboxHeader()
                    .data(creatorDetail.coverImageUrl)
                    .build(),
                loading = {
                    SimmerPlaceHolder()
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        } else if (creatorDetail.profileItems.isNotEmpty()) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(4.dp)),
                state = rememberPagerState { creatorDetail.profileItems.size },
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = ImageRequest.Builder(LocalContext.current)
                        .fanboxHeader()
                        .data(creatorDetail.profileItems[it].thumbnailUrl)
                        .build(),
                    loading = {
                        FadePlaceHolder()
                    },
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            UserSection(
                modifier = Modifier.fillMaxWidth(),
                creatorDetail = creatorDetail,
                isFollowed = isFollowed,
                onClickFollow = onClickFollow,
                onClickUnfollow = onClickUnfollow,
            )

            if (creatorDetail.description.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .animateContentSize(),
                        text = creatorDetail.description.trimEnd(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (isDisplayedAll) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = {
                            isEllipsized = it.hasVisualOverflow
                        },
                    )

                    if (isEllipsized && !isDisplayedAll) {
                        IconButton(onClick = { isDisplayedAll = true }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserSection(
    creatorDetail: FanboxCreatorDetail,
    isFollowed: Boolean,
    onClickFollow: (String) -> Unit,
    onClickUnfollow: (String) -> Unit,
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
                .error(R.drawable.im_default_user)
                .data(creatorDetail.user.iconUrl)
                .build(),
            contentDescription = null,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = creatorDetail.user.name,
                style = MaterialTheme.typography.titleMedium.bold(),
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "@${creatorDetail.user.creatorId}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (isFollowed) {
            OutlinedButton(onClick = { onClickUnfollow.invoke(creatorDetail.user.userId) }) {
                Text(stringResource(R.string.common_unfollow))
            }
        } else {
            Button(onClick = { onClickFollow.invoke(creatorDetail.user.userId) }) {
                Text(stringResource(R.string.common_follow))
            }
        }
    }
}

@Preview
@Composable
private fun CreatorItemPreview() {
    CreatorItem(
        creatorDetail = FanboxCreatorDetail.dummy(),
        onClickCreator = {},
        onClickFollow = {},
        onClickUnfollow = {},
    )
}
