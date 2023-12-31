package caios.android.fanbox.feature.library.notify

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import caios.android.fanbox.core.model.fanbox.FanboxBell
import caios.android.fanbox.core.model.fanbox.id.PostId
import caios.android.fanbox.core.ui.LazyPagingItemsLoadContents
import caios.android.fanbox.core.ui.component.PixiViewTopBar
import caios.android.fanbox.core.ui.extensition.LocalNavigationType
import caios.android.fanbox.core.ui.extensition.PixiViewNavigationType
import caios.android.fanbox.core.ui.extensition.drawVerticalScrollbar
import caios.android.fanbox.core.ui.view.PagingErrorSection
import caios.android.fanbox.feature.library.R
import caios.android.fanbox.feature.library.notify.items.LibraryNotifyBellItem

@Composable
internal fun LibraryNotifyRoute(
    openDrawer: () -> Unit,
    navigateToPostDetail: (PostId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryNotifyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val paging = uiState.paging.collectAsLazyPagingItems()

    LibraryNotifyScreen(
        modifier = modifier,
        openDrawer = openDrawer,
        onClickBell = navigateToPostDetail,
        pagingAdapter = paging,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryNotifyScreen(
    pagingAdapter: LazyPagingItems<FanboxBell>,
    openDrawer: () -> Unit,
    onClickBell: (PostId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigationType = LocalNavigationType.current.type
    val state = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PixiViewTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.library_navigation_notify),
                navigationIcon = Icons.Default.Menu,
                onClickNavigation = if (navigationType != PixiViewNavigationType.PermanentNavigationDrawer) openDrawer else null,
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            Divider()
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        LazyPagingItemsLoadContents(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            lazyPagingItems = pagingAdapter,
            emptyMessageRes = R.string.error_no_data_notify,
        ) {
            LazyColumn(
                modifier = Modifier.drawVerticalScrollbar(state),
                state = state,
            ) {
                items(
                    count = pagingAdapter.itemCount,
                    key = pagingAdapter.itemKey {
                        when (it) {
                            is FanboxBell.Comment -> "comment-${it.id}"
                            is FanboxBell.Like -> "like-${it.id}"
                            is FanboxBell.PostPublished -> "post-${it.id}"
                        }
                    },
                    contentType = pagingAdapter.itemContentType(),
                ) { index ->
                    pagingAdapter[index]?.let { bell ->
                        LibraryNotifyBellItem(
                            modifier = Modifier.fillMaxWidth(),
                            bell = bell,
                            onClickBell = onClickBell,
                        )

                        Divider()
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
}
