package caios.android.fanbox.feature.creator.follow

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
import caios.android.fanbox.core.model.fanbox.FanboxCreatorDetail
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.ui.AsyncLoadContents
import caios.android.fanbox.core.ui.component.CreatorItem
import caios.android.fanbox.core.ui.component.PixiViewTopBar
import caios.android.fanbox.core.ui.extensition.drawVerticalScrollbar
import caios.android.fanbox.core.ui.view.EmptyView
import caios.android.fanbox.feature.creator.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
internal fun FollowingCreatorsRoute(
    navigateToCreatorPlans: (CreatorId) -> Unit,
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FollowingCreatorsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = { viewModel.fetch() },
    ) { uiState ->
        FollowingCreatorsScreen(
            modifier = Modifier.fillMaxSize(),
            followingCreators = uiState.followingCreators.toImmutableList(),
            onClickCreator = navigateToCreatorPlans,
            onClickFollow = viewModel::follow,
            onClickUnfollow = viewModel::unfollow,
            onClickSupporting = { context.startActivity(Intent(Intent.ACTION_VIEW, it)) },
            terminate = terminate,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FollowingCreatorsScreen(
    followingCreators: ImmutableList<FanboxCreatorDetail>,
    onClickCreator: (CreatorId) -> Unit,
    onClickFollow: suspend (String) -> Result<Unit>,
    onClickUnfollow: suspend (String) -> Result<Unit>,
    onClickSupporting: (Uri) -> Unit,
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PixiViewTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.library_navigation_following),
                onClickNavigation = terminate,
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            Divider()
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        if (followingCreators.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .drawVerticalScrollbar(state),
                state = state,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(followingCreators.toList()) { followingCreator ->
                    var isFollowed by rememberSaveable(followingCreator.isFollowed) { mutableStateOf(followingCreator.isFollowed) }

                    CreatorItem(
                        modifier = Modifier.fillMaxWidth(),
                        creatorDetail = followingCreator,
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

                item {
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        } else {
            EmptyView(
                modifier = Modifier.fillMaxSize(),
                titleRes = R.string.error_no_data,
                messageRes = R.string.error_no_data_following,
            )
        }
    }
}
