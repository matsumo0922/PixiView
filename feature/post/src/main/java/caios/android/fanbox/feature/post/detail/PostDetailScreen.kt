package caios.android.fanbox.feature.post.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import caios.android.fanbox.core.common.util.ToastUtil
import caios.android.fanbox.core.model.ScreenState
import caios.android.fanbox.core.model.UserData
import caios.android.fanbox.core.model.contract.PostDownloader
import caios.android.fanbox.core.model.fanbox.FanboxCreatorDetail
import caios.android.fanbox.core.model.fanbox.FanboxMetaData
import caios.android.fanbox.core.model.fanbox.FanboxPost
import caios.android.fanbox.core.model.fanbox.FanboxPostDetail
import caios.android.fanbox.core.model.fanbox.id.CommentId
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.model.fanbox.id.PostId
import caios.android.fanbox.core.ui.AsyncLoadContents
import caios.android.fanbox.core.ui.LazyPagingItemsLoadContents
import caios.android.fanbox.core.ui.component.CoordinatorScaffold
import caios.android.fanbox.core.ui.component.RestrictCardItem
import caios.android.fanbox.core.ui.component.TagItems
import caios.android.fanbox.core.ui.extensition.FadePlaceHolder
import caios.android.fanbox.core.ui.extensition.fanboxHeader
import caios.android.fanbox.core.ui.extensition.isNullOrEmpty
import caios.android.fanbox.core.ui.extensition.marquee
import caios.android.fanbox.core.ui.theme.bold
import caios.android.fanbox.core.ui.theme.center
import caios.android.fanbox.core.ui.view.ErrorView
import caios.android.fanbox.core.ui.view.SimpleAlertContents
import caios.android.fanbox.feature.post.R
import caios.android.fanbox.feature.post.detail.items.PostDetailArticleHeader
import caios.android.fanbox.feature.post.detail.items.PostDetailCommentLikeButton
import caios.android.fanbox.feature.post.detail.items.PostDetailCommentSection
import caios.android.fanbox.feature.post.detail.items.PostDetailCreatorSection
import caios.android.fanbox.feature.post.detail.items.PostDetailDownloadSection
import caios.android.fanbox.feature.post.detail.items.PostDetailFileHeader
import caios.android.fanbox.feature.post.detail.items.PostDetailImageHeader
import caios.android.fanbox.feature.post.detail.items.PostDetailMenuDialog
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.collections.immutable.toImmutableList
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PostDetailRoute(
    postId: PostId,
    type: PostDetailPagingType,
    navigateToPostSearch: (String, CreatorId) -> Unit,
    navigateToPostDetail: (PostId) -> Unit,
    navigateToPostImage: (PostId, Int) -> Unit,
    navigateToCreatorPlans: (CreatorId) -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
    navigateToCommentDeleteDialog: (SimpleAlertContents, () -> Unit) -> Unit,
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostDetailRootViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val paging = uiState.paging?.collectAsLazyPagingItems()

    LaunchedEffect(true) {
        if (paging == null) {
            viewModel.fetch(type)
        }
    }

    if (!paging.isNullOrEmpty()) {
        LazyPagingItemsLoadContents(
            modifier = modifier,
            lazyPagingItems = paging!!,
        ) {
            val initIndex = remember { paging.itemSnapshotList.indexOfFirst { it?.id == postId } }
            val pagerState = rememberPagerState(if (initIndex != -1) initIndex else 0) { paging.itemCount }

            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) {
                paging[it]?.let { post ->
                    PostDetailView(
                        modifier = Modifier.fillMaxSize(),
                        postId = post.id,
                        navigateToPostSearch = navigateToPostSearch,
                        navigateToPostDetail = navigateToPostDetail,
                        navigateToPostImage = navigateToPostImage,
                        navigateToCreatorPlans = navigateToCreatorPlans,
                        navigateToCreatorPosts = navigateToCreatorPosts,
                        navigateToCommentDeleteDialog = navigateToCommentDeleteDialog,
                        terminate = terminate,
                    )
                }
            }
        }
    } else if (paging != null) {
        PostDetailView(
            modifier = modifier,
            postId = postId,
            navigateToPostSearch = navigateToPostSearch,
            navigateToPostDetail = navigateToPostDetail,
            navigateToPostImage = navigateToPostImage,
            navigateToCreatorPlans = navigateToCreatorPlans,
            navigateToCreatorPosts = navigateToCreatorPosts,
            navigateToCommentDeleteDialog = navigateToCommentDeleteDialog,
            terminate = terminate,
        )
    } else {
        ErrorView(
            modifier = Modifier.fillMaxSize(),
            errorState = ScreenState.Error(R.string.error_network),
            retryAction = { terminate.invoke() },
        )
    }
}

@Composable
private fun PostDetailView(
    postId: PostId,
    navigateToPostSearch: (String, CreatorId) -> Unit,
    navigateToPostDetail: (PostId) -> Unit,
    navigateToPostImage: (PostId, Int) -> Unit,
    navigateToCreatorPlans: (CreatorId) -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
    navigateToCommentDeleteDialog: (SimpleAlertContents, () -> Unit) -> Unit,
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = hiltViewModel(key = postId.value),
) {
    val context = LocalContext.current
    val postDownloader = context as PostDownloader
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(postId) {
        if (screenState !is ScreenState.Idle) {
            viewModel.fetch(postId)
        }
    }

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = { viewModel.fetch(postId) },
    ) { uiState ->
        PostDetailScreen(
            modifier = Modifier.fillMaxSize(),
            postDetail = uiState.postDetail,
            creatorDetail = uiState.creatorDetail,
            userData = uiState.userData,
            metaData = uiState.metaData,
            onClickPost = navigateToPostDetail,
            onClickPostLike = viewModel::postLike,
            onClickPostBookmark = viewModel::postBookmark,
            onClickCommentLoadMore = viewModel::loadMoreComment,
            onClickCommentLike = viewModel::commentLike,
            onClickCommentReply = viewModel::commentReply,
            onClickCommentDelete = {
                navigateToCommentDeleteDialog.invoke(SimpleAlertContents.CommentDelete) {
                    viewModel.commentDelete(it)
                }
            },
            onClickTag = { navigateToPostSearch.invoke(it, uiState.postDetail.user.creatorId) },
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
            onClickFollow = viewModel::follow,
            onClickUnfollow = viewModel::unfollow,
            onClickOpenBrowser = { context.startActivity(Intent(Intent.ACTION_VIEW, it)) },
            onTerminate = terminate,
        )

        LaunchedEffect(uiState.messageToast) {
            uiState.messageToast?.let { ToastUtil.show(context, it) }
            viewModel.consumeToast()
        }
    }
}

@Composable
private fun PostDetailScreen(
    postDetail: FanboxPostDetail,
    creatorDetail: FanboxCreatorDetail,
    userData: UserData,
    metaData: FanboxMetaData,
    onClickPost: (PostId) -> Unit,
    onClickPostLike: (PostId) -> Unit,
    onClickPostBookmark: (FanboxPost, Boolean) -> Unit,
    onClickCommentLoadMore: (PostId, Int) -> Unit,
    onClickCommentLike: (CommentId) -> Unit,
    onClickCommentReply: (PostId, String, CommentId, CommentId) -> Unit,
    onClickCommentDelete: (CommentId) -> Unit,
    onClickTag: (String) -> Unit,
    onClickCreator: (CreatorId) -> Unit,
    onClickImage: (FanboxPostDetail.ImageItem) -> Unit,
    onClickFile: (FanboxPostDetail.FileItem) -> Unit,
    onClickDownloadImages: (List<FanboxPostDetail.ImageItem>) -> Unit,
    onClickCreatorPosts: (CreatorId) -> Unit,
    onClickCreatorPlans: (CreatorId) -> Unit,
    onClickFollow: (String) -> Unit,
    onClickUnfollow: (String) -> Unit,
    onClickOpenBrowser: (Uri) -> Unit,
    onTerminate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowMenu by remember { mutableStateOf(false) }
    var isPostLiked by rememberSaveable(postDetail.isLiked) { mutableStateOf(postDetail.isLiked) }
    var isBookmarked by remember(postDetail.isBookmarked) { mutableStateOf(postDetail.isBookmarked) }

    val isShowCoordinateHeader = when (val content = postDetail.body) {
        is FanboxPostDetail.Body.Article -> content.blocks.first() !is FanboxPostDetail.Body.Article.Block.Image
        is FanboxPostDetail.Body.File -> true
        is FanboxPostDetail.Body.Image -> false
        is FanboxPostDetail.Body.Unknown -> true
    }

    CoordinatorScaffold(
        modifier = modifier,
        onClickNavigateUp = onTerminate,
        onClickMenu = { isShowMenu = true },
        verticalArrangement = Arrangement.spacedBy(16.dp),
        header = {
            if (!isShowCoordinateHeader) {
                PostDetailContent(
                    modifier = it,
                    post = postDetail,
                    userData = userData,
                    onClickCreator = onClickCreator,
                    onClickPost = onClickPost,
                    onClickPostLike = onClickPostLike,
                    onClickPostBookmark = onClickPostBookmark,
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
                    userData = userData,
                    onClickCreator = onClickCreator,
                    onClickPost = onClickPost,
                    onClickPostLike = onClickPostLike,
                    onClickPostBookmark = onClickPostBookmark,
                    onClickImage = onClickImage,
                    onClickFile = onClickFile,
                    onClickDownload = onClickDownloadImages,
                )
            }
        }

        if (postDetail.tags.isNotEmpty()) {
            item {
                TagItems(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    tags = postDetail.tags.toImmutableList(),
                    onClickTag = onClickTag,
                )
            }
        }

        item {
            PostDetailCommentLikeButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                isLiked = isPostLiked,
                isBookmarked = isBookmarked,
                likeCount = postDetail.likeCount,
                commentCount = postDetail.commentCount,
                onClickLike = {
                    isPostLiked = true
                    onClickPostLike.invoke(postDetail.id)
                },
                onClickBookmark = {
                    isBookmarked = it
                    onClickPostBookmark.invoke(postDetail.asPost(), isBookmarked)
                },
            )
        }

        if (postDetail.isRestricted) {
            item {
                RestrictCardItem(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
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
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    postDetail = postDetail,
                    onClickDownload = onClickDownloadImages,
                )
            }
        }

        item {
            PostDetailCreatorSection(
                modifier = Modifier.fillMaxWidth(),
                postDetail = postDetail,
                creatorDetail = creatorDetail,
                onClickCreator = { onClickCreatorPosts.invoke(it) },
                onClickFollow = onClickFollow,
                onClickUnfollow = onClickUnfollow,
                onClickSupporting = onClickOpenBrowser,
            )
        }

        item {
            PostDetailCommentSection(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                postDetail = postDetail,
                metaData = metaData,
                onClickLoadMore = onClickCommentLoadMore,
                onClickCommentLike = onClickCommentLike,
                onClickCommentReply = { body, parent, root -> onClickCommentReply.invoke(postDetail.id, body, parent, root) },
                onClickCommentDelete = onClickCommentDelete,
            )
        }

        item {
            Spacer(modifier = Modifier.height(128.dp))
        }
    }

    if (isShowMenu) {
        PostDetailMenuDialog(
            onClickOpenBrowser = { onClickOpenBrowser.invoke(postDetail.browserUri) },
            onClickAllDownload = { onClickDownloadImages.invoke(postDetail.body.imageItems) },
            onDismissRequest = { isShowMenu = false },
        )
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
                    FadePlaceHolder()
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
    userData: UserData,
    onClickPost: (PostId) -> Unit,
    onClickPostLike: (PostId) -> Unit,
    onClickPostBookmark: (FanboxPost, Boolean) -> Unit,
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
                    userData = userData,
                    isAdultContents = post.hasAdultContent,
                    onClickPost = onClickPost,
                    onClickPostLike = onClickPostLike,
                    onClickPostBookmark = onClickPostBookmark,
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
                    isAdultContents = post.hasAdultContent,
                    isOverrideAdultContents = userData.isAllowedShowAdultContents,
                    isTestUser = userData.isTestUser,
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

@Preview
@Composable
private fun PostDetailScreenPreview() {
    PostDetailScreen(
        postDetail = FanboxPostDetail.dummy(),
        creatorDetail = FanboxCreatorDetail.dummy(),
        userData = UserData.dummy(),
        metaData = FanboxMetaData.dummy(),
        onClickPost = {},
        onClickPostLike = {},
        onClickPostBookmark = { _, _ -> },
        onClickCommentLoadMore = { _, _ -> },
        onClickCommentLike = {},
        onClickCommentReply = { _, _, _, _ -> },
        onClickCommentDelete = {},
        onClickTag = {},
        onClickCreator = {},
        onClickImage = {},
        onClickFile = {},
        onClickDownloadImages = {},
        onClickCreatorPosts = {},
        onClickCreatorPlans = {},
        onClickFollow = {},
        onClickUnfollow = {},
        onClickOpenBrowser = {},
        onTerminate = {},
    )
}
