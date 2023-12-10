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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import caios.android.fanbox.core.model.ScreenState
import caios.android.fanbox.core.model.UserData
import caios.android.fanbox.core.model.fanbox.FanboxCreatorDetail
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.ui.AsyncLoadContents
import caios.android.fanbox.core.ui.component.CreatorItem
import caios.android.fanbox.core.ui.component.PixiViewTopBar
import caios.android.fanbox.core.ui.extensition.drawVerticalScrollbar
import caios.android.fanbox.core.ui.theme.bold
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
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        if (screenState !is ScreenState.Idle) {
            viewModel.fetch()
        }
    }

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = viewModel::fetch,
    ) { uiState ->
        LibraryDiscoveryScreen(
            modifier = Modifier.fillMaxSize(),
            recommendedCreators = uiState.recommendedCreators,
            followingPixivCreators = uiState.followingPixivCreators,
            openDrawer = openDrawer,
            onClickSearch = navigateToPostSearch,
            onClickCreator = navigateToCreatorPosts,
            onClickFollow = viewModel::follow,
            onClickUnfollow = viewModel::unfollow,
            onClickSupporting = { context.startActivity(Intent(Intent.ACTION_VIEW, it)) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryDiscoveryScreen(
    recommendedCreators: List<FanboxCreatorDetail>,
    followingPixivCreators: List<FanboxCreatorDetail>,
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
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .drawVerticalScrollbar(state),
            state = state,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (recommendedCreators.isNotEmpty()) {
                item {
                    TitleItem(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.creator_recommended),
                    )
                }

                items(
                    items = recommendedCreators,
                    key = { item -> "recommended-${item.creatorId.value}" },
                ) {
                    var isFollowed by rememberSaveable { mutableStateOf(it.isFollowed) }

                    CreatorItem(
                        modifier = Modifier.fillMaxWidth(),
                        creatorDetail = it,
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

            if (followingPixivCreators.isNotEmpty()) {
                item {
                    TitleItem(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.creator_following_pixiv),
                    )
                }

                items(
                    items = followingPixivCreators,
                    key = { item -> "pixiv-${item.creatorId.value}" },
                ) {
                    var isFollowed by rememberSaveable { mutableStateOf(it.isFollowed) }

                    CreatorItem(
                        modifier = Modifier.fillMaxWidth(),
                        creatorDetail = it,
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

            item {
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
}

@Composable
private fun TitleItem(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = title.uppercase(),
        style = MaterialTheme.typography.bodyMedium.bold(),
        color = MaterialTheme.colorScheme.primary,
    )
}
