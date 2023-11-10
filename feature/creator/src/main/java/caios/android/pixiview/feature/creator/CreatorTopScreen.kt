package caios.android.pixiview.feature.creator

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlan
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.feature.creator.items.CreatorTopHeader
import caios.android.pixiview.feature.creator.items.CreatorTopPlansScreen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@Composable
internal fun CreatorTopRoute(
    creatorId: CreatorId,
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreatorTopViewModel = hiltViewModel(),
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(creatorId) {
        viewModel.fetch(creatorId)
    }

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = { viewModel.fetch(creatorId) },
    ) { uiState ->
        CreatorTopScreen(
            modifier = Modifier.fillMaxSize(),
            creatorDetail = uiState.creatorDetail,
            creatorPlans = uiState.creatorPlans.toImmutableList(),
            onClickPlan = { context.startActivity(Intent(Intent.ACTION_VIEW, it.detailUri)) },
            onTerminate = terminate,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun CreatorTopScreen(
    creatorDetail: FanboxCreatorDetail,
    creatorPlans: ImmutableList<FanboxCreatorPlan>,
    onClickPlan: (FanboxCreatorPlan) -> Unit,
    onTerminate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberCollapsingToolbarScaffoldState()
    val pagerState = rememberPagerState { 2 }
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
