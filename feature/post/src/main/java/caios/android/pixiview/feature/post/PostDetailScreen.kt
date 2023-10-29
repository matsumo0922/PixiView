package caios.android.pixiview.feature.post

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.AsyncLoadContents

@Composable
internal fun PostDetailRoute(
    postId: PostId,
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = hiltViewModel(),
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(postId) {
        viewModel.fetch(postId)
    }

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = { viewModel.fetch(postId) },
    ) {
        PostDetailScreen(
            modifier = Modifier.fillMaxSize(),
            postDetail = it.postDetail,
        )
    }
}

@Composable
private fun PostDetailScreen(
    postDetail: FanboxPostDetail,
    modifier: Modifier = Modifier,
) {

}
