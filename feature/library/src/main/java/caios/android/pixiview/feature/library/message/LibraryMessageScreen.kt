package caios.android.pixiview.feature.library.message

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.pixiview.core.model.fanbox.FanboxNewsLetter
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.component.PixiViewTopBar
import caios.android.pixiview.core.ui.extensition.drawVerticalScrollbar
import caios.android.pixiview.feature.library.R
import caios.android.pixiview.feature.library.message.items.LibraryMessageItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun LibraryMessageRoute(
    openDrawer: () -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryMessageViewModel = hiltViewModel(),
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = viewModel::fetch,
    ) { uiState ->
        LibraryMessageScreen(
            modifier = Modifier.fillMaxSize(),
            messages = uiState.messages.toImmutableList(),
            openDrawer = openDrawer,
            onClickCreator = navigateToCreatorPosts,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryMessageScreen(
    messages: ImmutableList<FanboxNewsLetter>,
    openDrawer: () -> Unit,
    onClickCreator: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PixiViewTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.library_navigation_message),
                navigationIcon = Icons.Default.Menu,
                onClickNavigation = openDrawer,
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            HorizontalDivider()
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .drawVerticalScrollbar(state),
            state = state,
        ) {
            items(
                items = messages,
                key = { it.id.value },
            ) {
                LibraryMessageItem(
                    modifier = Modifier.fillMaxWidth(),
                    message = it,
                    onClickCreator = onClickCreator,
                )

                HorizontalDivider()
            }
        }
    }
}
