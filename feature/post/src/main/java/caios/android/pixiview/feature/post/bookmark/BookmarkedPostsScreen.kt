package caios.android.pixiview.feature.post.bookmark

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.component.PostItem
import caios.android.pixiview.core.ui.extensition.drawVerticalScrollbar
import caios.android.pixiview.core.ui.view.ErrorView
import caios.android.pixiview.feature.post.R
import caios.android.pixiview.feature.post.bookmark.items.BookmarkedPostsTopBar
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun BookmarkedPostsRoute(
    navigateToPostDetail: (PostId) -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
    navigateToCreatorPlans: (CreatorId) -> Unit,
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookmarkedPostsViewModel = hiltViewModel(),
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        if (screenState !is ScreenState.Idle) {
            viewModel.fetch()
        }
    }

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
    ) {
        BookmarkedPostsScreen(
            modifier = Modifier.fillMaxSize(),
            userData = it.userData,
            bookmarkedPosts = it.bookmarkedPosts.toImmutableList(),
            onSearch = viewModel::search,
            onClickPost = navigateToPostDetail,
            onClickPostBookmark = viewModel::postBookmark,
            onClickCreatorPosts = navigateToCreatorPosts,
            onClickCreatorPlans = navigateToCreatorPlans,
            onTerminate = terminate,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun BookmarkedPostsScreen(
    userData: UserData,
    bookmarkedPosts: ImmutableList<FanboxPost>,
    onSearch: (String) -> Unit,
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
            BookmarkedPostsTopBar(
                modifier = Modifier.fillMaxWidth(),
                onClickSearch = onSearch,
                onClickTerminate = onTerminate,
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            Divider()
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            AnimatedContent(
                targetState = bookmarkedPosts.isNotEmpty(),
                transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                label = "BookmarkedPostsScreen",
            ) { targetState ->
                if (targetState) {
                    LazyColumn(
                        modifier = Modifier.drawVerticalScrollbar(state),
                        state = state,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(
                            items = bookmarkedPosts,
                            key = { it.id.value },
                        ) { likedPost ->
                            PostItem(
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .fillMaxWidth(),
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
                        title = R.string.bookmark_empty_title,
                        message = R.string.bookmark_empty_description,
                    )
                }
            }
        }
    }
}
