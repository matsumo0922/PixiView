package caios.android.pixiview.core.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.ui.view.ErrorView
import caios.android.pixiview.core.ui.view.LoadingView

@Composable
fun <T : Any> LazyPagingItemsLoadSurface(
    lazyPagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    emptyComponent: @Composable () -> Unit = {},
    content: @Composable (CombinedLoadStates) -> Unit,
) {
    Surface(modifier) {
        if (lazyPagingItems.isEmpty()) {
            emptyComponent.invoke()
        } else {
            lazyPagingItems.apply {
                when (loadState.refresh) {
                    is LoadState.Loading -> {
                        LoadingView()
                    }
                    is LoadState.Error -> {
                        ErrorView(
                            errorState = ScreenState.Error(R.string.error_network, R.string.common_retry),
                            retryAction = { lazyPagingItems.refresh() },
                        )
                    }
                    is LoadState.NotLoading -> {
                        content.invoke(loadState)
                    }
                }
            }
        }
    }
}

private fun <T : Any> LazyPagingItems<T>.isEmpty(): Boolean {
    val isNotLoading = loadState.refresh is LoadState.NotLoading
    val isReachedEnd = loadState.append.endOfPaginationReached
    val isEmptyItems = itemCount == 0

    return isNotLoading && isReachedEnd && isEmptyItems
}
