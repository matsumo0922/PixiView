package caios.android.pixiview.feature.creator.top

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlan
import caios.android.pixiview.core.model.fanbox.FanboxCreatorTag
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.LazyPagingItemsLoadContents
import caios.android.pixiview.core.ui.component.CollapsingToolbarScaffold
import caios.android.pixiview.core.ui.component.ScrollStrategy
import caios.android.pixiview.core.ui.component.rememberCollapsingToolbarScaffoldState
import caios.android.pixiview.feature.creator.R
import caios.android.pixiview.feature.creator.top.items.CreatorTopDescriptionDialog
import caios.android.pixiview.feature.creator.top.items.CreatorTopHeader
import caios.android.pixiview.feature.creator.top.items.CreatorTopPlansScreen
import caios.android.pixiview.feature.creator.top.items.CreatorTopPostsScreen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
internal fun CreatorTopRoute(
    creatorId: CreatorId,
    isPosts: Boolean,
    navigateToPostDetail: (PostId) -> Unit,
    navigateToPostSearch: (String, CreatorId) -> Unit,
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreatorTopViewModel = hiltViewModel(),
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(creatorId) {
        if (screenState !is ScreenState.Idle) {
            viewModel.fetch(creatorId)
        }
    }

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = { viewModel.fetch(creatorId) },
    ) { uiState ->
        CreatorTopScreen(
            modifier = Modifier.fillMaxSize(),
            isPosts = isPosts,
            userData = uiState.userData,
            bookmarkedPosts = uiState.bookmarkedPosts.toImmutableList(),
            creatorDetail = uiState.creatorDetail,
            creatorPlans = uiState.creatorPlans.toImmutableList(),
            creatorTags = uiState.creatorTags.toImmutableList(),
            creatorPostsPaging = uiState.creatorPostsPaging.collectAsLazyPagingItems(),
            onClickPost = navigateToPostDetail,
            onClickPlan = { context.startActivity(Intent(Intent.ACTION_VIEW, it.browserUri)) },
            onClickTag = { navigateToPostSearch.invoke(it.tag, uiState.creatorDetail.creatorId) },
            onTerminate = terminate,
            onClickLink = { context.startActivity(Intent(Intent.ACTION_VIEW, it.toUri())) },
            onClickFollow = viewModel::follow,
            onClickUnfollow = viewModel::unfollow,
            onClickPostBookmark = viewModel::postBookmark,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun CreatorTopScreen(
    isPosts: Boolean,
    creatorDetail: FanboxCreatorDetail,
    userData: UserData,
    bookmarkedPosts: List<PostId>,
    creatorPlans: ImmutableList<FanboxCreatorPlan>,
    creatorTags: ImmutableList<FanboxCreatorTag>,
    creatorPostsPaging: LazyPagingItems<FanboxPost>,
    onClickPost: (PostId) -> Unit,
    onClickPostBookmark: (FanboxPost, Boolean) -> Unit,
    onClickPlan: (FanboxCreatorPlan) -> Unit,
    onClickTag: (FanboxCreatorTag) -> Unit,
    onClickLink: (String) -> Unit,
    onClickFollow: (String) -> Unit,
    onClickUnfollow: (String) -> Unit,
    onTerminate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberCollapsingToolbarScaffoldState()
    val pagerState = rememberPagerState(initialPage = if (isPosts) 0 else 1) { 2 }
    val scope = rememberCoroutineScope()

    val postsListState = rememberLazyListState()
    val plansListState = rememberLazyListState()

    var isShowDescriptionDialog by remember { mutableStateOf(false) }

    val tabs = listOf(
        CreatorTab.POSTS,
        CreatorTab.PLANS,
    )

    CollapsingToolbarScaffold(
        modifier = modifier,
        state = state,
        toolbarModifier = Modifier.verticalScroll(rememberScrollState()),
        toolbar = {
            Spacer(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth(),
            )

            CreatorTopHeader(
                modifier = Modifier
                    .parallax(1f)
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = state.toolbarState.progress * 10
                    },
                creatorDetail = creatorDetail,
                onClickTerminate = onTerminate,
                onClickMenu = {},
                onClickLink = onClickLink,
                onClickFollow = onClickFollow,
                onClickUnfollow = onClickUnfollow,
                onClickDescription = { isShowDescriptionDialog = true },
            )
        },
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
    ) {
        Column(Modifier.fillMaxSize()) {
            TabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = pagerState.currentPage,
            ) {
                tabs.forEachIndexed { index, tab ->
                    CreatorTab(
                        isSelected = pagerState.currentPage == index,
                        label = stringResource(tab.titleRes),
                        onClick = {
                            scope.launch {
                                if (pagerState.currentPage != index) {
                                    pagerState.animateScrollToPage(index)
                                } else {
                                    when (tabs[index]) {
                                        CreatorTab.POSTS -> postsListState.animateScrollToItem(0)
                                        CreatorTab.PLANS -> plansListState.animateScrollToItem(0)
                                    }
                                }
                            }
                        },
                    )
                }
            }

            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) {
                when (tabs[it]) {
                    CreatorTab.POSTS -> {
                        LazyPagingItemsLoadContents(
                            modifier = Modifier.fillMaxSize(),
                            lazyPagingItems = creatorPostsPaging,
                            isSwipeEnabled = state.toolbarState.progress == 1f,
                        ) {
                            CreatorTopPostsScreen(
                                modifier = Modifier.fillMaxSize(),
                                state = postsListState,
                                userData = userData,
                                bookmarkedPosts = bookmarkedPosts,
                                pagingAdapter = creatorPostsPaging,
                                creatorTags = creatorTags,
                                onClickPost = onClickPost,
                                onClickPostBookmark = onClickPostBookmark,
                                onClickTag = onClickTag,
                                onClickCreator = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(1)
                                    }
                                },
                                onClickPlanList = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(1)
                                    }
                                },
                            )
                        }
                    }
                    CreatorTab.PLANS -> {
                        CreatorTopPlansScreen(
                            modifier = Modifier.fillMaxSize(),
                            state = plansListState,
                            creatorPlans = creatorPlans,
                            onClickPlan = onClickPlan,
                        )
                    }
                }
            }
        }
    }

    if (isShowDescriptionDialog) {
        CreatorTopDescriptionDialog(
            description = creatorDetail.description,
            onDismissRequest = { isShowDescriptionDialog = false },
        )
    }
}

@Composable
private fun CreatorTab(
    isSelected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Tab(
        modifier = modifier,
        text = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
        },
        selected = isSelected,
        onClick = onClick,
    )
}

private enum class CreatorTab(val titleRes: Int) {
    POSTS(R.string.creator_tab_posts),
    PLANS(R.string.creator_tab_plans),
}
