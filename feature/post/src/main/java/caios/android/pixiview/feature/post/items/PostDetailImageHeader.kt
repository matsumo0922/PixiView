package caios.android.pixiview.feature.post.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

        if (content.text.isNotBlank()) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = content.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
