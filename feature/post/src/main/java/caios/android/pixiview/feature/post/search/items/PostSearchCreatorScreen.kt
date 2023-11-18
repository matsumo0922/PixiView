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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.ui.LazyPagingItemsLoadContents
import caios.android.pixiview.core.ui.component.CreatorItem
import caios.android.pixiview.core.ui.extensition.drawVerticalScrollbar
import caios.android.pixiview.core.ui.view.PagingErrorSection
import kotlinx.coroutines.launch

@Composable
internal fun PostSearchCreatorScreen(
    pagingAdapter: LazyPagingItems<FanboxCreatorDetail>,
    onClickCreator: (CreatorId) -> Unit,
    onClickFollow: suspend (String) -> Result<Unit>,
    onClickUnfollow: suspend (String) -> Result<Unit>,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
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
                key = pagingAdapter.itemKey(),
                contentType = pagingAdapter.itemContentType(),
            ) { index ->
                pagingAdapter[index]?.let { creatorDetail ->
                    var isFollowed by rememberSaveable { mutableStateOf(creatorDetail.isFollowed) }

                    CreatorItem(
                        modifier = Modifier.fillMaxWidth(),
                        creatorDetail = creatorDetail,
                        isFollowed = isFollowed,
                        onClickCreator = onClickCreator,
                        onClickFollow = {
                            scope.launch {
                                isFollowed = true
                                isFollowed = onClickFollow.invoke(it).isSuccess
                            }
                        },
                        onClickUnfollow = {
                            scope.launch {
                                isFollowed = false
                                isFollowed = !onClickUnfollow.invoke(it).isSuccess
                            }
                        },
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
