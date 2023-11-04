package caios.android.pixiview.feature.post.detail.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.ui.component.PostItem

@Composable
internal fun PostDetailArticleHeader(
    content: FanboxPostDetail.Body.Article,
    onClickPost: (PostId) -> Unit,
    onClickImage: (FanboxPostDetail.ImageItem) -> Unit,
    onClickFile: (FanboxPostDetail.FileItem) -> Unit,
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
                        modifier = Modifier.fillMaxWidth(),
                        item = item.item,
                        onClickImage = onClickImage,
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
                        onClickPost = onClickPost,
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
    onClickPost: (PostId) -> Unit,
    modifier: Modifier = Modifier,
) {
    item.post?.also {
        PostItem(
            modifier = modifier.padding(16.dp),
            post = it,
            onClickPost = onClickPost,
            onClickPlanList = {},
        )
    }
}
