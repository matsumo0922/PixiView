package caios.android.pixiview.feature.post.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.pixiview.core.common.util.ToastUtil
import caios.android.pixiview.core.model.contract.PostDownloader
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.component.CoordinatorScaffold
import caios.android.pixiview.core.ui.component.RestrictCardItem
import caios.android.pixiview.core.ui.component.TagItems
import caios.android.pixiview.core.ui.extensition.SimmerPlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import caios.android.pixiview.core.ui.extensition.marquee
import caios.android.pixiview.core.ui.theme.bold
import caios.android.pixiview.core.ui.theme.center
import caios.android.pixiview.feature.post.detail.items.PostDetailArticleHeader
import caios.android.pixiview.feature.post.detail.items.PostDetailCommentLikeButton
import caios.android.pixiview.feature.post.detail.items.PostDetailCommentSection
import caios.android.pixiview.feature.post.detail.items.PostDetailCreatorSection
import caios.android.pixiview.feature.post.detail.items.PostDetailDownloadSection
import caios.android.pixiview.feature.post.detail.items.PostDetailFileHeader
import caios.android.pixiview.feature.post.detail.items.PostDetailImageHeader
import caios.android.pixiview.feature.post.detail.items.PostDetailOtherPostSection
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.collections.immutable.toImmutableList
import java.time.format.DateTimeFormatter

@Composable
internal fun PostDetailRoute(
    postId: PostId,
    navigateToPostDetail: (PostId) -> Unit,
    navigateToPostImage: (PostId, Int) -> Unit,
    navigateToCreatorPlans: (CreatorId) -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val postDownloader = context as PostDownloader
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(postId) {
        viewModel.fetch(postId)
    }

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = { viewModel.fetch(postId) },
    ) { uiState ->
        PostDetailScreen(
            modifier = Modifier.fillMaxSize(),
            postDetail = uiState.postDetail,
            onClickPost = navigateToPostDetail,
            onClickCreator = navigateToCreatorPlans,
            onClickImage = { item ->
                uiState.postDetail.body.imageItems.indexOf(item).let { index ->
                    navigateToPostImage.invoke(postId, index)
                }
            },
            onClickFile = postDownloader::onDownloadFile,
            onClickDownloadImages = postDownloader::onDownloadImages,
            onClickCreatorPosts = navigateToCreatorPosts,
            onClickCreatorPlans = navigateToCreatorPlans,
            onTerminate = terminate,
        )

        LaunchedEffect(uiState.messageToast) {
            uiState.messageToast?.let { ToastUtil.show(context, it) }
        }
    }
}

@Composable
private fun PostDetailScreen(
    postDetail: FanboxPostDetail,
    onClickPost: (PostId) -> Unit,
    onClickCreator: (CreatorId) -> Unit,
    onClickImage: (FanboxPostDetail.ImageItem) -> Unit,
    onClickFile: (FanboxPostDetail.FileItem) -> Unit,
    onClickDownloadImages: (List<FanboxPostDetail.ImageItem>) -> Unit,
    onClickCreatorPosts: (CreatorId) -> Unit,
    onClickCreatorPlans: (CreatorId) -> Unit,
    onTerminate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isShowCoordinateHeader = when (val content = postDetail.body) {
        is FanboxPostDetail.Body.Article -> content.blocks.first() !is FanboxPostDetail.Body.Article.Block.Image
        is FanboxPostDetail.Body.File -> true
        is FanboxPostDetail.Body.Image -> false
        is FanboxPostDetail.Body.Unknown -> true
    }

    CoordinatorScaffold(
        modifier = modifier,
        onClickNavigateUp = onTerminate,
        header = {
            if (!isShowCoordinateHeader) {
                PostDetailContent(
                    modifier = it,
                    post = postDetail,
                    onClickCreator = onClickCreator,
                    onClickPost = onClickPost,
                    onClickImage = onClickImage,
                    onClickFile = onClickFile,
                    onClickDownload = onClickDownloadImages,
                )
            } else {
                PostDetailHeader(
                    modifier = it,
                    post = postDetail,
                )
            }
        },
    ) {
        if (isShowCoordinateHeader) {
            item {
                PostDetailContent(
                    modifier = Modifier.fillMaxWidth(),
                    post = postDetail,
                    onClickCreator = onClickCreator,
                    onClickPost = onClickPost,
                    onClickImage = onClickImage,
                    onClickFile = onClickFile,
                    onClickDownload = onClickDownloadImages,
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TagItems(
                    modifier = Modifier.weight(1f),
                    tags = postDetail.tags.toImmutableList(),
                    onClickTag = { },
                )

                PostDetailCommentLikeButton(
                    commentCount = postDetail.commentCount,
                    likeCount = postDetail.likeCount,
                )
            }
        }

        if (postDetail.isRestricted) {
            item {
                RestrictCardItem(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    feeRequired = postDetail.feeRequired,
                    backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
                    onClickPlanList = { onClickCreatorPlans.invoke(postDetail.user.creatorId) },
                )
            }
        } else if (postDetail.body.imageItems.isNotEmpty()) {
            item {
                PostDetailDownloadSection(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    postDetail = postDetail,
                    onClickDownload = onClickDownloadImages,
                )
            }
        }

        item {
            PostDetailCreatorSection(
                modifier = Modifier.fillMaxWidth(),
                post = postDetail,
                onClickCreator = { onClickCreatorPlans.invoke(it) },
            )
        }

        if (postDetail.commentCount != 0) {
            item {
                PostDetailCommentSection(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    post = postDetail,
                )
            }
        }

        item {
            PostDetailOtherPostSection(
                modifier = Modifier.fillMaxWidth(),
                nextPost = postDetail.nextPost,
                previousPost = postDetail.prevPost,
                onClickNextPost = onClickPost,
                onClickPreviousPost = onClickPost,
                onClickAllPosts = { onClickCreatorPosts.invoke(postDetail.user.creatorId) },
            )
        }

        item {
            Spacer(modifier = Modifier.height(128.dp))
        }
    }
}

@Composable
private fun PostDetailHeader(
    post: FanboxPostDetail,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        if (post.coverImageUrl != null) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4 / 3f),
                model = ImageRequest.Builder(LocalContext.current)
                    .fanboxHeader()
                    .data(post.coverImageUrl)
                    .build(),
                loading = {
                    SimmerPlaceHolder()
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        } else {
            FileThumbnail(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4 / 3f),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .align(Alignment.TopCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.surface))),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .marquee(),
                text = post.title,
                style = MaterialTheme.typography.headlineSmall.center().bold(),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                text = post.publishedDatetime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                style = MaterialTheme.typography.bodyMedium.center(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PostDetailContent(
    post: FanboxPostDetail,
    onClickPost: (PostId) -> Unit,
    onClickCreator: (CreatorId) -> Unit,
    onClickImage: (FanboxPostDetail.ImageItem) -> Unit,
    onClickFile: (FanboxPostDetail.FileItem) -> Unit,
    onClickDownload: (List<FanboxPostDetail.ImageItem>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        when (val content = post.body) {
            is FanboxPostDetail.Body.Article -> {
                PostDetailArticleHeader(
                    modifier = Modifier.fillMaxWidth(),
                    content = content,
                    onClickPost = onClickPost,
                    onClickCreator = onClickCreator,
                    onClickImage = onClickImage,
                    onClickFile = onClickFile,
                    onClickDownload = onClickDownload,
                )
            }

            is FanboxPostDetail.Body.Image -> {
                PostDetailImageHeader(
                    modifier = Modifier.fillMaxWidth(),
                    content = content,
                    onClickImage = onClickImage,
                    onClickDownload = onClickDownload,
                )
            }

            is FanboxPostDetail.Body.File -> {
                PostDetailFileHeader(
                    modifier = Modifier.fillMaxWidth(),
                    content = content,
                    onClickFile = onClickFile,
                )
            }

            is FanboxPostDetail.Body.Unknown -> {
                // do nothing
            }
        }
    }
}

@Composable
private fun FileThumbnail(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color.DarkGray)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically,
        ),
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
            tint = Color.White,
            contentDescription = null,
        )
    }
}
