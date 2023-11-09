package caios.android.pixiview.feature.creator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.component.CoordinatorScaffold
import caios.android.pixiview.feature.creator.items.CreatorTopHeader

@Composable
internal fun CreatorTopRoute(
    creatorId: CreatorId,
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreatorTopViewModel = hiltViewModel(),
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(creatorId) {
        viewModel.fetch(creatorId)
    }

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = { viewModel.fetch(creatorId) },
    ) {
        CreatorTopScreen(
            modifier = Modifier.fillMaxSize(),
            creatorDetail = it.creator,
            onTerminate = terminate,
        )
    }
}

@Composable
private fun CreatorTopScreen(
    creatorDetail: FanboxCreatorDetail,
    onTerminate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CoordinatorScaffold(
        modifier = modifier,
        onClickNavigateUp = onTerminate,
        onClickMenu = {},
        header = {
            CreatorTopHeader(
                modifier = it,
                creatorDetail = creatorDetail,
            )
        },
    ) {

    }
}
