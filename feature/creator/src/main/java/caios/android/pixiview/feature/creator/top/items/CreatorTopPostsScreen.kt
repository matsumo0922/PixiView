package caios.android.pixiview.feature.creator.top.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.model.fanbox.FanboxCreatorTag
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.component.PostItem
import caios.android.pixiview.core.ui.extensition.FadePlaceHolder
import caios.android.pixiview.core.ui.extensition.drawVerticalScrollbar
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import caios.android.pixiview.core.ui.theme.bold
import caios.android.pixiview.core.ui.view.PagingErrorSection
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun CreatorTopPostsScreen(
    state: LazyListState,
    userData: UserData,
    bookmarkedPosts: ImmutableList<PostId>,
    pagingAdapter: LazyPagingItems<FanboxPost>,
    creatorTags: ImmutableList<FanboxCreatorTag>,
    onClickPost: (PostId) -> Unit,
    onClickPostBookmark: (FanboxPost, Boolean) -> Unit,
    onClickCreator: (CreatorId) -> Unit,
    onClickTag: (FanboxCreatorTag) -> Unit,
    onClickPlanList: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.drawVerticalScrollbar(state),
        state = state,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (creatorTags.isNotEmpty()) {
            item {
                LazyRow(
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(creatorTags) {
                        TagItem(
                            tag = it,
                            onClickTag = onClickTag,
                        )
                    }
                }
            }
        }

        items(
            count = pagingAdapter.itemCount,
            key = pagingAdapter.itemKey { it.id.value },
            contentType = pagingAdapter.itemContentType(),
        ) { index ->
            pagingAdapter[index]?.let { post ->
                PostItem(
                    modifier = Modifier.fillMaxWidth(),
                    post = post.copy(isBookmarked = bookmarkedPosts.contains(post.id)),
                    isHideAdultContents = userData.isHideAdultContents,
                    onClickPost = { if (!post.isRestricted) onClickPost.invoke(it) },
                    onClickCreator = onClickCreator,
                    onClickPlanList = onClickPlanList,
                    onClickBookmark = { _, isBookmarked -> onClickPostBookmark.invoke(post, isBookmarked) },
                )
            }
        }

        if (pagingAdapter.loadState.append is LoadState.Error) {
            item {
                PagingErrorSection(
                    modifier = Modifier.fillMaxWidth(),
                    onRetry = { pagingAdapter.retry() },
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(128.dp))
        }
    }
}

@Composable
private fun TagItem(
    tag: FanboxCreatorTag,
    onClickTag: (FanboxCreatorTag) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(5 / 2f, true)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClickTag.invoke(tag) },
    ) {
        if (tag.coverImageUrl != null) {
            SubcomposeAsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .fanboxHeader()
                    .data(tag.coverImageUrl)
                    .build(),
                loading = {
                    FadePlaceHolder()
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondaryContainer),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        )

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.CenterHorizontally,
            ),
        ) {
            Text(
                modifier = Modifier.weight(1f, fill = false),
                text = "#${tag.name}",
                style = MaterialTheme.typography.bodyLarge.bold().copy(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(1f, 1f),
                        blurRadius = 3f,
                    ),
                ),
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis,
            )

            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Text(
                    modifier = Modifier.padding(8.dp, 6.dp),
                    text = tag.count.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
