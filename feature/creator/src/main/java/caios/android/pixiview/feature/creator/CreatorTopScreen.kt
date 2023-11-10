package caios.android.pixiview.feature.creator

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlan
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.component.CoordinatorScaffold
import caios.android.pixiview.feature.creator.items.CreatorTopDescriptionSection
import caios.android.pixiview.feature.creator.items.CreatorTopHeader
import caios.android.pixiview.feature.creator.items.CreatorTopPlansSection

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
            creatorPlans = uiState.creatorPlans,
            onClickPlan = { context.startActivity(Intent(Intent.ACTION_VIEW, it.detailUri)) },
            onTerminate = terminate,
        )
    }
}

@Composable
private fun CreatorTopScreen(
    creatorDetail: FanboxCreatorDetail,
    creatorPlans: List<FanboxCreatorPlan>,
    onClickPlan: (FanboxCreatorPlan) -> Unit,
    onTerminate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CoordinatorScaffold(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        onClickNavigateUp = onTerminate,
        onClickMenu = {},
        header = {
            CreatorTopHeader(
                modifier = it,
                creatorDetail = creatorDetail,
            )
        },
    ) {
        item {
            CreatorTopDescriptionSection(
                modifier = Modifier.fillMaxWidth(),
                creatorDetail = creatorDetail,
            )
        }

        item {
            CreatorTopPlansSection(
                modifier = Modifier.fillMaxWidth(),
                creatorPlans = creatorPlans,
                onClickPlan = onClickPlan,
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(128.dp))
        }
    }
}
