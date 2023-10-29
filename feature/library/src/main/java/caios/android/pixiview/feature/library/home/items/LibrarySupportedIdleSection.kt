package caios.android.pixiview.feature.library.home.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.ui.component.PostItem
import caios.android.pixiview.core.ui.extensition.drawVerticalScrollbar

@Composable
internal fun LibrarySupportedIdleSection(
    pagingAdapter: LazyPagingItems<FanboxPost>,
    onClickPost: (FanboxPost) -> Unit,
    onClickPlanList: (String) -> Unit,
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
            key = pagingAdapter.itemKey { it.id },
            contentType = pagingAdapter.itemContentType(),
        ) { index ->
            pagingAdapter[index]?.let {
                PostItem(
                    modifier = Modifier.fillMaxWidth(),
                    post = it,
                    onClickPost = onClickPost,
                    onClickPlanList = onClickPlanList,
                )
            }
        }
    }
}
