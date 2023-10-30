package caios.android.pixiview.feature.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.component.CoordinatorScaffold
import caios.android.pixiview.core.ui.component.RestrictCardItem
import caios.android.pixiview.core.ui.component.TagItems
import caios.android.pixiview.core.ui.extensition.SimmerPlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import caios.android.pixiview.feature.post.items.PostDetailArticleHeader
import caios.android.pixiview.feature.post.items.PostDetailCommentLikeButton
import caios.android.pixiview.feature.post.items.PostDetailDownloadSection
import caios.android.pixiview.feature.post.items.PostDetailFileHeader
import caios.android.pixiview.feature.post.items.PostDetailImageHeader
import caios.android.pixiview.feature.post.items.PostDetailUserSection
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
internal fun PostDetailRoute(
    postId: PostId,
    modifier: Modifier = Modifier,
    navigateToPostDetail: (PostId) -> Unit,
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
            onClickPost = navigateToPostDetail,
            onClickImage = { },
            onClickFile = { },
            onClickDownloadImages = { },
            onTerminate = terminate,
        )
    }
}

@Composable
private fun PostDetailScreen(
    postDetail: FanboxPostDetail,
    onClickPost: (PostId) -> Unit,
    onClickImage: (FanboxPostDetail.ImageItem) -> Unit,
    onClickFile: (FanboxPostDetail.FileItem) -> Unit,
    onClickDownloadImages: (List<FanboxPostDetail.ImageItem>) -> Unit,
    onTerminate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CoordinatorScaffold(
        modifier = modifier,
        onClickNavigateUp = onTerminate,
        onClickMenu = {},
        header = {
            PostDetailHeader(
                modifier = it,
                post = postDetail,
                onClickPost = onClickPost,
                onClickImage = onClickImage,
                onClickFile = onClickFile,
            )
        }
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TagItems(
                    modifier = Modifier.weight(1f),
                    tags = postDetail.tags,
                    onClickTag = {},
                )

                PostDetailCommentLikeButton(
                    commentCount = postDetail.commentCount,
                    likeCount = postDetail.likeCount,
                )
            }
        }

        item {
            HorizontalDivider()
        }

        item {
            if (postDetail.isRestricted) {
                RestrictCardItem(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    feeRequired = postDetail.feeRequired,
                    backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
                    onClickPlanList = { },
                )
            } else {
                PostDetailDownloadSection(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    postDetail = postDetail,
                    onClickDownload = onClickDownloadImages,
                )
            }
        }

        items(listOf(Color.Blue, Color.Red, Color.Yellow, Color.Cyan)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp)
                    .background(it)
            )
            postDetail.user
        }
    }
}

@Composable
private fun PostDetailHeader(
    post: FanboxPostDetail,
    onClickPost: (PostId) -> Unit,
    onClickImage: (FanboxPostDetail.ImageItem) -> Unit,
    onClickFile: (FanboxPostDetail.FileItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (post.coverImageUrl != null) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f),
                model = ImageRequest.Builder(LocalContext.current)
                    .fanboxHeader()
                    .crossfade(true)
                    .data(post.coverImageUrl)
                    .build(),
                loading = {
                    SimmerPlaceHolder()
                },
                contentDescription = null,
            )
        }

        PostDetailUserSection(
            modifier = Modifier.fillMaxWidth(),
            post = post,
        )

        HorizontalDivider()

        when (val content = post.body) {
            is FanboxPostDetail.Body.Article -> {
                PostDetailArticleHeader(
                    modifier = Modifier.fillMaxWidth(),
                    content = content,
                    onClickPost = onClickPost,
                    onClickImage = onClickImage,
                    onClickFile = onClickFile,
                )
            }

            is FanboxPostDetail.Body.File -> {
                PostDetailFileHeader(
                    modifier = Modifier.fillMaxWidth(),
                    content = content,
                    onClickFile = onClickFile,
                )
            }

            is FanboxPostDetail.Body.Image -> {
                PostDetailImageHeader(
                    modifier = Modifier.fillMaxWidth(),
                    content = content,
                    onClickImage = onClickImage,
                )
            }

            else -> {

            }
        }
    }
}
