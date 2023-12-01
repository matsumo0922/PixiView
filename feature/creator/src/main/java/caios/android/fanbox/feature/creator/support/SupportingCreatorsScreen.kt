package caios.android.fanbox.feature.creator.support

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.fanbox.core.model.fanbox.FanboxCreatorPlan
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.ui.AsyncLoadContents
import caios.android.fanbox.core.ui.component.PixiViewTopBar
import caios.android.fanbox.core.ui.extensition.drawVerticalScrollbar
import caios.android.fanbox.feature.creator.R
import caios.android.fanbox.feature.creator.support.item.SupportingCreatorItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun SupportingCreatorsRoute(
    navigateToCreatorPlans: (CreatorId) -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
    navigateToFanCard: (CreatorId) -> Unit,
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SupportingCreatorsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = { viewModel.fetch() },
    ) { uiState ->
        SupportingCreatorsScreen(
            modifier = Modifier.fillMaxSize(),
            supportedCreators = uiState.supportedPlans.toImmutableList(),
            onClickPlanDetail = { context.startActivity(Intent(Intent.ACTION_VIEW, it)) },
            onClickFanCard = navigateToFanCard,
            onClickCreatorPlans = navigateToCreatorPlans,
            onClickCreatorPosts = navigateToCreatorPosts,
            terminate = terminate,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupportingCreatorsScreen(
    supportedCreators: ImmutableList<FanboxCreatorPlan>,
    onClickPlanDetail: (Uri) -> Unit,
    onClickFanCard: (CreatorId) -> Unit,
    onClickCreatorPlans: (CreatorId) -> Unit,
    onClickCreatorPosts: (CreatorId) -> Unit,
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PixiViewTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.library_navigation_supporting),
                onClickNavigation = terminate,
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
            items(supportedCreators.toList()) { supportingPlan ->
                SupportingCreatorItem(
                    modifier = Modifier.fillMaxWidth(),
                    supportingPlan = supportingPlan,
                    onClickPlanDetail = onClickPlanDetail,
                    onClickFanCard = onClickFanCard,
                    onClickCreatorPlans = onClickCreatorPlans,
                    onClickCreatorPosts = onClickCreatorPosts,
                )
            }

            item {
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
}
