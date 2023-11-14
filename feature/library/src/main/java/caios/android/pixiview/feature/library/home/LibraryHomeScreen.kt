package caios.android.pixiview.feature.library.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.LazyPagingItemsLoadContents
import caios.android.pixiview.feature.library.R
import caios.android.pixiview.feature.library.home.items.LibraryHomeIdleSection
import caios.android.pixiview.feature.library.home.items.LibrarySupportedIdleSection
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun LibraryHomeScreen(
    openDrawer: () -> Unit,
    navigateToPostDetail: (PostId) -> Unit,
    navigateToCreatorPlans: (CreatorId) -> Unit,
    navigateToCreatorTop: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryHomeViewModel = hiltViewModel(),
) {
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val supportedUiState by viewModel.supportedUiState.collectAsStateWithLifecycle()

    val homePager = homeUiState.paging.collectAsLazyPagingItems()
    val supportedPager = supportedUiState.paging.collectAsLazyPagingItems()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(snapAnimationSpec = null, flingAnimationSpec = null)

    val pagerState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()

    val tabs = listOf(
        HomeTabs.Home,
        HomeTabs.Supported,
    )

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(text = "PixiView")
                },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null,
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        Column(Modifier.padding(padding)) {
            PrimaryTabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = pagerState.currentPage,
            ) {
                tabs.forEachIndexed { index, tab ->
                    HomeTab(
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
                    HomeTabs.Home -> {
                        LazyPagingItemsLoadContents(
                            modifier = Modifier.fillMaxSize(),
                            lazyPagingItems = homePager,
                        ) {
                            LibraryHomeIdleSection(
                                modifier = Modifier.fillMaxSize(),
                                pagingAdapter = homePager,
                                onClickPost = navigateToPostDetail,
                                onClickCreator = navigateToCreatorTop,
                                onClickPlanList = navigateToCreatorPlans,
                            )
                        }
                    }
                    HomeTabs.Supported -> {
                        LazyPagingItemsLoadContents(
                            modifier = Modifier.fillMaxSize(),
                            lazyPagingItems = supportedPager,
                        ) {
                            LibrarySupportedIdleSection(
                                modifier = Modifier.fillMaxSize(),
                                pagingAdapter = supportedPager,
                                onClickPost = navigateToPostDetail,
                                onClickCreator = navigateToCreatorTop,
                                onClickPlanList = navigateToCreatorPlans,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTab(
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

private enum class HomeTabs(val titleRes: Int) {
    Home(R.string.home_tab_home),
    Supported(R.string.home_tab_supported),
}
