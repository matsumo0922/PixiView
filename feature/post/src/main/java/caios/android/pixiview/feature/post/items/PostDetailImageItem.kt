package caios.android.pixiview.feature.post.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.ui.extensition.SimmerPlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
internal fun PostDetailImageItem(
    item: FanboxPostDetail.ImageItem,
    onClickImage: (FanboxPostDetail.ImageItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    SubcomposeAsyncImage(
        modifier = modifier
            .aspectRatio(item.aspectRatio)
            .clickable { onClickImage.invoke(item) },
        model = ImageRequest.Builder(LocalContext.current)
            .fanboxHeader()
            .crossfade(true)
            .data(item.thumbnailUrl)
            .build(),
        loading = {
            SimmerPlaceHolder()
        },
        contentDescription = null,
    )
}
