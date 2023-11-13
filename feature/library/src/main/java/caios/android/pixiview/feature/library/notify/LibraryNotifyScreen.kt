package caios.android.pixiview.feature.library.notify

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import caios.android.pixiview.core.model.fanbox.FanboxBell
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.LazyPagingItemsLoadSurface
import caios.android.pixiview.core.ui.component.PixiViewTopBar
import caios.android.pixiview.core.ui.extensition.drawVerticalScrollbar
import caios.android.pixiview.feature.library.R
import caios.android.pixiview.feature.library.notify.items.LibraryNotifyBellItem

@Composable
internal fun LibraryNotifyRoute(
    openDrawer: () -> Unit,
    navigateToPostDetail: (PostId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryNotifyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val paging = uiState.paging.collectAsLazyPagingItems()

    LazyPagingItemsLoadSurface(
        modifier = modifier,
        lazyPagingItems = paging,
    ) {
        LibraryNotifyScreen(
            modifier = Modifier.fillMaxSize(),
            openDrawer = openDrawer,
            onClickBell = navigateToPostDetail,
            pagingAdapter = paging,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryNotifyScreen(
    pagingAdapter: LazyPagingItems<FanboxBell>,
    openDrawer: () -> Unit,
    onClickBell: (PostId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PixiViewTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.library_navigation_notify),
                navigationIcon = Icons.Default.Menu,
                onClickNavigation = openDrawer,
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            HorizontalDivider()
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .drawVerticalScrollbar(state),
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

                    HorizontalDivider()
                }
            }
        }
    }
}
