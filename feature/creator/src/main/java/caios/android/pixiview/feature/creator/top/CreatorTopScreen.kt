package caios.android.pixiview.feature.creator.top

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlan
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.LazyPagingItemsLoadContents
import caios.android.pixiview.feature.creator.R
import caios.android.pixiview.feature.creator.top.items.CreatorTopHeader
import caios.android.pixiview.feature.creator.top.items.CreatorTopPlansScreen
import caios.android.pixiview.feature.creator.top.items.CreatorTopPostsScreen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@Composable
internal fun CreatorTopRoute(
    creatorId: CreatorId,
    isPosts: Boolean,
    navigateToPostDetail: (PostId) -> Unit,
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
            creatorDetail = uiState.creatorDetail,
            creatorPlans = uiState.creatorPlans.toImmutableList(),
            creatorPostsPaging = uiState.creatorPostsPaging.collectAsLazyPagingItems(),
            onClickPost = navigateToPostDetail,
            onClickPlan = { context.startActivity(Intent(Intent.ACTION_VIEW, it.browserUri)) },
            onTerminate = terminate,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CreatorTopScreen(
    isPosts: Boolean,
    creatorDetail: FanboxCreatorDetail,
    creatorPlans: ImmutableList<FanboxCreatorPlan>,
    creatorPostsPaging: LazyPagingItems<FanboxPost>,
    onClickPost: (PostId) -> Unit,
    onClickPlan: (FanboxCreatorPlan) -> Unit,
    onTerminate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberCollapsingToolbarScaffoldState()
    val pagerState = rememberPagerState(initialPage = if (isPosts) 1 else 0) { 2 }
    val scope = rememberCoroutineScope()

    val tabs = listOf(
        CreatorTab.PLANS,
        CreatorTab.POSTS,
    )

    CollapsingToolbarScaffold(
        modifier = modifier,
        state = state,
        toolbar = {
            Spacer(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth(),
            )

            CreatorTopHeader(
                modifier = Modifier
                    .parallax(0.5f)
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = state.toolbarState.progress
                    },
                creatorDetail = creatorDetail,
                onClickTerminate = onTerminate,
                onClickMenu = {},
            )
        },
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
    ) {
        Column(Modifier.fillMaxSize()) {
            PrimaryTabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = pagerState.currentPage,
            ) {
                tabs.forEachIndexed { index, tab ->
                    CreatorTab(
                        isSelected = pagerState.currentPage == index,
                        label = stringResource(tab.titleRes),
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    )
                }
            }

            HorizontalPager(pagerState) {
                when (tabs[it]) {
                    CreatorTab.POSTS -> {
                        LazyPagingItemsLoadContents(
                            modifier = Modifier.fillMaxWidth(),
                            lazyPagingItems = creatorPostsPaging,
                        ) {
                            CreatorTopPostsScreen(
                                modifier = Modifier.fillMaxSize(),
                                pagingAdapter = creatorPostsPaging,
                                onClickPost = onClickPost,
                                onClickCreator = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(0)
                                    }
                                },
                                onClickPlanList = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(0)
                                    }
                                },
                            )
                        }
                    }
                    CreatorTab.PLANS -> {
                        CreatorTopPlansScreen(
                            modifier = Modifier.fillMaxWidth(),
                            creatorDetail = creatorDetail,
                            creatorPlans = creatorPlans,
                            onClickPlan = onClickPlan,
                        )
                    }
                }
            }
        }
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
