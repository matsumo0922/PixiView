package caios.android.pixiview.feature.post.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail

@Composable
internal fun PostDetailFileHeader(
    content: FanboxPostDetail.Body.File,
    onClickFile: (FanboxPostDetail.FileItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        for (item in content.files) {
            PostDetailFileItem(
                modifier = Modifier.fillMaxWidth(),
                item = item,
                onClickDownload = onClickFile,
            )
        }
    }
}
