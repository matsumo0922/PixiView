package caios.android.pixiview.feature.post.bookmark

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.component.PixiViewTopBar
import caios.android.pixiview.core.ui.component.PostItem
import caios.android.pixiview.core.ui.extensition.drawVerticalScrollbar
import caios.android.pixiview.core.ui.view.ErrorView
import caios.android.pixiview.feature.post.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun BookmarkedPostsRoute(
    navigateToPostDetail: (PostId) -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
    navigateToCreatorPlans: (CreatorId) -> Unit,
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LikedPostsViewModel = hiltViewModel(),
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
    ) {
        BookmarkedPostsScreen(
            modifier = Modifier.fillMaxSize(),
            userData = it.userData,
            bookmarkedPosts = it.bookmarkedPosts.toImmutableList(),
            onClickPost = navigateToPostDetail,
            onClickPostBookmark = viewModel::postBookmark,
            onClickCreatorPosts = navigateToCreatorPosts,
            onClickCreatorPlans = navigateToCreatorPlans,
            onTerminate = terminate,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookmarkedPostsScreen(
    userData: UserData,
    bookmarkedPosts: ImmutableList<FanboxPost>,
    onClickPost: (PostId) -> Unit,
    onClickPostBookmark: (FanboxPost, Boolean) -> Unit,
    onClickCreatorPosts: (CreatorId) -> Unit,
    onClickCreatorPlans: (CreatorId) -> Unit,
    onTerminate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PixiViewTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.library_navigation_bookmark),
                onClickNavigation = onTerminate,
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            HorizontalDivider()
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            if (bookmarkedPosts.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.drawVerticalScrollbar(state),
                    state = state,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(bookmarkedPosts) { likedPost ->
                        PostItem(
                            modifier = Modifier.fillMaxWidth(),
                            post = likedPost,
                            isHideAdultContents = userData.isHideAdultContents,
                            onClickPost = { if (!likedPost.isRestricted) onClickPost.invoke(it) },
                            onClickBookmark = { _, isBookmarked -> onClickPostBookmark.invoke(likedPost, isBookmarked) },
                            onClickCreator = onClickCreatorPosts,
                            onClickPlanList = onClickCreatorPlans,
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }
            } else {
                ErrorView(
                    modifier = Modifier.fillMaxSize(),
                    errorState = ScreenState.Error(R.string.error_no_data),
                    retryAction = null,
                )
            }
        }
    }
}
