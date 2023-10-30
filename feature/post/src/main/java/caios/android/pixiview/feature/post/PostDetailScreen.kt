package caios.android.pixiview.feature.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.component.CoordinatorScaffold
import caios.android.pixiview.feature.post.items.PostDetailArticleHeader
import caios.android.pixiview.feature.post.items.PostDetailFileHeader
import caios.android.pixiview.feature.post.items.PostDetailImageHeader

@Composable
internal fun PostDetailRoute(
    postId: PostId,
    modifier: Modifier = Modifier,
    terminate: () -> Unit,
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
            onClickImage = { },
            onClickFile = { },
            onTerminate = terminate,
        )
    }
}

@Composable
private fun PostDetailScreen(
    postDetail: FanboxPostDetail,
    onClickImage: (FanboxPostDetail.ImageItem) -> Unit,
    onClickFile: (FanboxPostDetail.FileItem) -> Unit,
    onTerminate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CoordinatorScaffold(
        modifier = modifier,
        onClickNavigateUp = onTerminate,
        onClickMenu = {},
        header = {
            when (val content = postDetail.body) {
                is FanboxPostDetail.Body.Article -> {
                    PostDetailArticleHeader(
                        modifier = it,
                        content = content,
                        onClickImage = onClickImage,
                        onClickFile = onClickFile,
                    )
                }

                is FanboxPostDetail.Body.File -> {
                    PostDetailFileHeader(
                        modifier = it,
                        content = content,
                        onClickFile = onClickFile,
                    )
                }

                is FanboxPostDetail.Body.Image -> {
                    PostDetailImageHeader(
                        modifier = it,
                        content = content,
                        onClickImage = onClickImage,
                    )
                }

                else -> {

                }
            }
        }
    ) {
        items(listOf(Color.Blue, Color.Red, Color.Yellow, Color.Cyan)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp)
                    .background(it)
            )
        }
    }
}
