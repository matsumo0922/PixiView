package caios.android.pixiview.feature.post.search.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.LazyPagingItemsLoadContents
import caios.android.pixiview.core.ui.component.PostItem
import caios.android.pixiview.core.ui.extensition.drawVerticalScrollbar
import caios.android.pixiview.core.ui.view.PagingErrorSection

@Composable
internal fun PostSearchTagScreen(
    pagingAdapter: LazyPagingItems<FanboxPost>,
    userData: UserData,
    bookmarkedPosts: List<PostId>,
    onClickPost: (PostId) -> Unit,
    onClickPostBookmark: (FanboxPost, Boolean) -> Unit,
    onClickCreator: (CreatorId) -> Unit,
    onClickPlanList: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val state = rememberLazyListState()

    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo }.collect {
            keyboardController?.hide()
        }
    }

    LazyPagingItemsLoadContents(
        modifier = modifier,
        lazyPagingItems = pagingAdapter,
    ) {
        LazyColumn(
            modifier = Modifier.drawVerticalScrollbar(state),
            state = state,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
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
                        onClickBookmark = { _, isLiked -> onClickPostBookmark.invoke(post, isLiked) },
                        onClickCreator = onClickCreator,
                        onClickPlanList = onClickPlanList,
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
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
}
