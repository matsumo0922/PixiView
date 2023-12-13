package caios.android.fanbox.feature.library.home.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import caios.android.fanbox.core.model.UserData
import caios.android.fanbox.core.model.fanbox.FanboxPost
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.model.fanbox.id.PostId
import caios.android.fanbox.core.ui.ads.NativeAdMediumItem
import caios.android.fanbox.core.ui.ads.NativeAdsPreLoader
import caios.android.fanbox.core.ui.component.PostGridItem
import caios.android.fanbox.core.ui.component.PostItem
import caios.android.fanbox.core.ui.extensition.drawVerticalScrollbar
import caios.android.fanbox.core.ui.view.PagingErrorSection
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun LibrarySupportedIdleSection(
    pagingAdapter: LazyPagingItems<FanboxPost>,
    userData: UserData,
    nativeAdUnitId: String,
    nativeAdsPreLoader: NativeAdsPreLoader,
    bookmarkedPosts: ImmutableList<PostId>,
    onClickPost: (PostId) -> Unit,
    onClickPostLike: (PostId) -> Unit,
    onClickPostBookmark: (FanboxPost, Boolean) -> Unit,
    onClickCreator: (CreatorId) -> Unit,
    onClickPlanList: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (userData.isGridMode) {
        GridSection(
            pagingAdapter = pagingAdapter,
            userData = userData,
            bookmarkedPosts = bookmarkedPosts,
            onClickPost = onClickPost,
            modifier = modifier,
        )
    } else {
        ColumnSection(
            pagingAdapter = pagingAdapter,
            userData = userData,
            nativeAdUnitId = nativeAdUnitId,
            nativeAdsPreLoader = nativeAdsPreLoader,
            bookmarkedPosts = bookmarkedPosts,
            onClickPost = onClickPost,
            onClickPostLike = onClickPostLike,
            onClickPostBookmark = onClickPostBookmark,
            onClickCreator = onClickCreator,
            onClickPlanList = onClickPlanList,
            modifier = modifier,
        )
    }
}

@Composable
private fun ColumnSection(
    pagingAdapter: LazyPagingItems<FanboxPost>,
    userData: UserData,
    nativeAdUnitId: String,
    nativeAdsPreLoader: NativeAdsPreLoader,
    bookmarkedPosts: ImmutableList<PostId>,
    onClickPost: (PostId) -> Unit,
    onClickPostLike: (PostId) -> Unit,
    onClickPostBookmark: (FanboxPost, Boolean) -> Unit,
    onClickCreator: (CreatorId) -> Unit,
    onClickPlanList: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()

    LazyColumn(
        modifier = modifier.drawVerticalScrollbar(state),
        state = state,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            count = pagingAdapter.itemCount,
            key = pagingAdapter.itemKey { it.id.value },
            contentType = pagingAdapter.itemContentType(),
        ) { index ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                pagingAdapter[index]?.let { post ->
                    PostItem(
                        modifier = Modifier.fillMaxWidth(),
                        post = post.copy(isBookmarked = bookmarkedPosts.contains(post.id)),
                        isHideAdultContents = userData.isHideAdultContents,
                        isOverrideAdultContents = userData.isAllowedShowAdultContents,
                        isTestUser = userData.isTestUser,
                        onClickPost = { if (!post.isRestricted) onClickPost.invoke(it) },
                        onClickCreator = onClickCreator,
                        onClickPlanList = onClickPlanList,
                        onClickLike = onClickPostLike,
                        onClickBookmark = { _, isBookmarked ->
                            onClickPostBookmark.invoke(post, isBookmarked)
                        },
                    )
                }

                if ((index + 4) % 5 == 0 && !userData.hasPrivilege) {
                    NativeAdMediumItem(
                        modifier = Modifier.fillMaxWidth(),
                        nativeAdUnitId = nativeAdUnitId,
                        nativeAdsPreLoader = nativeAdsPreLoader,
                    )
                }
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

@Composable
private fun GridSection(
    pagingAdapter: LazyPagingItems<FanboxPost>,
    userData: UserData,
    bookmarkedPosts: ImmutableList<PostId>,
    onClickPost: (PostId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyGridState()

    if (pagingAdapter.loadState.append !is LoadState.Error) {
        LazyVerticalGrid(
            modifier = modifier
                .drawVerticalScrollbar(state, spanCount = 2)
                .fillMaxSize(),
            columns = GridCells.Fixed(2),
            state = state,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(
                count = pagingAdapter.itemCount,
                key = pagingAdapter.itemKey { it.id.value },
                contentType = pagingAdapter.itemContentType(),
            ) { index ->
                pagingAdapter[index]?.let { post ->
                    PostGridItem(
                        modifier = Modifier.fillMaxWidth(),
                        post = post.copy(isBookmarked = bookmarkedPosts.contains(post.id)),
                        isHideAdultContents = userData.isHideAdultContents,
                        isOverrideAdultContents = userData.isAllowedShowAdultContents,
                        onClickPost = { if (!post.isRestricted) onClickPost.invoke(it) },
                    )
                }
            }
        }
    } else {
        PagingErrorSection(
            modifier = Modifier.fillMaxSize(),
            onRetry = { pagingAdapter.retry() },
        )
    }
}
