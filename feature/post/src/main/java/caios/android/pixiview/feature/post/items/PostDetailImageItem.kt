package caios.android.pixiview.feature.post.items

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.ui.extensition.SimmerPlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest

@Composable
internal fun PostDetailImageItem(
    item: FanboxPostDetail.ImageItem,
    onClickImage: (FanboxPostDetail.ImageItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val loadUrl = if (item.extension.lowercase() == "gif") item.originalUrl else item.thumbnailUrl

    SubcomposeAsyncImage(
        modifier = modifier
            .aspectRatio(item.aspectRatio)
            .clickable { onClickImage.invoke(item) },
        model = ImageRequest.Builder(LocalContext.current)
            .fanboxHeader()
            .crossfade(true)
            .data(loadUrl)
            .build(),
        imageLoader = ImageLoader.Builder(LocalContext.current)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build(),
        loading = {
            SimmerPlaceHolder()
        },
        contentDescription = null,
    )
}
