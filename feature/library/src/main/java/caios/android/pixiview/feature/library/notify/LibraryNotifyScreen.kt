package caios.android.pixiview.feature.library.notify

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import caios.android.pixiview.core.model.fanbox.FanboxBell
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.component.PixiViewTopBar
import caios.android.pixiview.feature.library.R
import caios.android.pixiview.feature.library.notify.items.LibraryNotifyBellItem
import caios.android.pixiview.feature.library.notify.items.LibraryNotifyLoadMoreButton
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun LibraryNotifyRoute(
    openDrawer: () -> Unit,
    navigateToPostDetail: (PostId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryNotifyViewModel = hiltViewModel(),
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = { viewModel.fetch() },
    ) { uiState ->
        LibraryNotifyScreen(
            modifier = Modifier.fillMaxSize(),
            openDrawer = openDrawer,
            onClickBell = navigateToPostDetail,
            onClickLoadMore = viewModel::loadMore,
            bells = uiState.bells.toImmutableList(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryNotifyScreen(
    bells: ImmutableList<FanboxBell>,
    openDrawer: () -> Unit,
    onClickBell: (PostId) -> Unit,
    onClickLoadMore: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(stringResource(R.string.library_navigation_notify),)
                },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
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
            modifier = Modifier.padding(padding),
        ) {
            items(
                items = bells,
                key = {
                    when (it) {
                        is FanboxBell.Comment -> "comment-${it.id}"
                        is FanboxBell.Like -> "like-${it.id}"
                        is FanboxBell.PostPublished -> "post-${it.id}"
                    }
                },
            ) {
                LibraryNotifyBellItem(
                    modifier = Modifier.fillMaxWidth(),
                    bell = it,
                    onClickBell = onClickBell,
                )

                HorizontalDivider()
            }

            bells.lastOrNull()?.nextPage?.also {
                item {
                    LibraryNotifyLoadMoreButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onClickLoadMore.invoke(it) },
                    )
                }
            }
        }
    }
}
