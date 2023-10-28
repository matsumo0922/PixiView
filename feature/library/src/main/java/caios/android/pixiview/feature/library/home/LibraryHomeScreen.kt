package caios.android.pixiview.feature.library.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import caios.android.pixiview.core.ui.LazyPagingItemsLoadSurface
import caios.android.pixiview.feature.library.home.items.LibraryHomeIdleSection

@Composable
internal fun LibraryHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: LibraryHomeViewModel = hiltViewModel(),
) {
    val homeUiState by viewModel.homeScreenState.collectAsStateWithLifecycle()
    val supportedUiState by viewModel.supportedScreenState.collectAsStateWithLifecycle()

    val homePager = homeUiState.paging.collectAsLazyPagingItems()
    val supportedPager = supportedUiState.paging.collectAsLazyPagingItems()

    LazyPagingItemsLoadSurface(
        modifier = modifier,
        lazyPagingItems = homePager,
    ) {
        LibraryHomeIdleSection(
            modifier = Modifier.fillMaxSize(),
            pagingAdapter = homePager,
        )
    }
}
