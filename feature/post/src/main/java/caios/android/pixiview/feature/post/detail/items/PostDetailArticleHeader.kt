package caios.android.pixiview.feature.post.detail.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.component.PostItem

@Composable
internal fun PostDetailArticleHeader(
    content: FanboxPostDetail.Body.Article,
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
    Column(modifier) {
        for (item in content.blocks) {
            when (item) {
                is FanboxPostDetail.Body.Article.Block.Text -> {
                    ArticleTextItem(
                        modifier = Modifier.fillMaxWidth(),
                        item = item,
                    )
                }

                is FanboxPostDetail.Body.Article.Block.Image -> {
                    PostDetailImageItem(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth(),
                        item = item.item,
                        onClickImage = onClickImage,
                        onClickDownload = { onClickDownload.invoke(listOf(item.item)) },
                        onClickAllDownload = { onClickDownload.invoke(content.imageItems) },
                    )
                }

                is FanboxPostDetail.Body.Article.Block.File -> {
                    PostDetailFileItem(
                        modifier = Modifier.fillMaxWidth(),
                        item = item.item,
                        onClickDownload = onClickFile,
                    )
                }

                is FanboxPostDetail.Body.Article.Block.Link -> {
                    ArticleLinkItem(
                        modifier = Modifier.fillMaxWidth(),
                        item = item,
                        isHideAdultContents = userData.isHideAdultContents,
                        onClickPost = onClickPost,
                        onClickPostLike = onClickPostLike,
                        onClickPostBookmark = { _, isLiked -> item.post?.let { onClickPostBookmark.invoke(it, isLiked) } },
                        onClickCreator = onClickCreator,
                    )
                }
            }
        }
    }
}

@Composable
private fun ArticleTextItem(
    item: FanboxPostDetail.Body.Article.Block.Text,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.padding(16.dp),
        text = item.text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun ArticleLinkItem(
    item: FanboxPostDetail.Body.Article.Block.Link,
    isHideAdultContents: Boolean,
    onClickPost: (PostId) -> Unit,
    onClickPostLike: (PostId) -> Unit,
    onClickPostBookmark: (PostId, Boolean) -> Unit,
    onClickCreator: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    item.post?.also {
        PostItem(
            modifier = modifier.padding(16.dp),
            post = it,
            isHideAdultContents = isHideAdultContents,
            onClickPost = onClickPost,
            onClickCreator = onClickCreator,
            onClickPlanList = {},
            onClickLike = onClickPostLike,
            onClickBookmark = onClickPostBookmark,
        )
    }
}
