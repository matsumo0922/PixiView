package caios.android.pixiview.feature.post.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail

@Composable
internal fun PostDetailImageHeader(
    content: FanboxPostDetail.Body.Image,
    onClickImage: (FanboxPostDetail.ImageItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        for (item in content.images) {
            PostDetailImageItem(
                modifier = Modifier.fillMaxWidth(),
                item = item,
                onClickImage = onClickImage,
            )
        }
    }
}
