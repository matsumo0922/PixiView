package caios.android.fanbox.feature.library.discovery

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import caios.android.fanbox.core.model.fanbox.FanboxCreatorDetail
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.ui.LazyPagingItemsLoadContents
import caios.android.fanbox.core.ui.component.CreatorItem
import caios.android.fanbox.core.ui.component.PixiViewTopBar
import caios.android.fanbox.core.ui.extensition.drawVerticalScrollbar
import caios.android.fanbox.core.ui.view.PagingErrorSection
import caios.android.fanbox.feature.library.R
import kotlinx.coroutines.launch

@Composable
internal fun LibraryDiscoveryRoute(
    openDrawer: () -> Unit,
    navigateToPostSearch: () -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryDiscoveryViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val paging = uiState.paging.collectAsLazyPagingItems()

    LibraryDiscoveryScreen(
        modifier = modifier,
        openDrawer = openDrawer,
        onClickSearch = navigateToPostSearch,
        onClickCreator = navigateToCreatorPosts,
        onClickFollow = viewModel::follow,
        onClickUnfollow = viewModel::unfollow,
        onClickSupporting = { context.startActivity(Intent(Intent.ACTION_VIEW, it)) },
        pagingAdapter = paging,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryDiscoveryScreen(
    pagingAdapter: LazyPagingItems<FanboxCreatorDetail>,
    openDrawer: () -> Unit,
    onClickSearch: () -> Unit,
    onClickCreator: (CreatorId) -> Unit,
    onClickFollow: suspend (String) -> Result<Unit>,
    onClickUnfollow: suspend (String) -> Result<Unit>,
    onClickSupporting: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val state = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PixiViewTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.library_navigation_discovery),
                navigationIcon = Icons.Default.Menu,
                actionsIcon = Icons.Default.Search,
                onClickNavigation = openDrawer,
                onClickActions = onClickSearch,
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
            emptyMessageRes = R.string.error_no_data_discovery,
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
                            onClickSupporting = onClickSupporting,
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
}
