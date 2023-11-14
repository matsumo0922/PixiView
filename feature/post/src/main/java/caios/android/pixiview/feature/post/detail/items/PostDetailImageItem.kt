package caios.android.pixiview.feature.post.detail.items

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.ui.extensition.SimmerPlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import caios.android.pixiview.feature.post.image.items.PostImageMenuDialog
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PostDetailImageItem(
    item: FanboxPostDetail.ImageItem,
    onClickImage: (FanboxPostDetail.ImageItem) -> Unit,
    onClickDownload: () -> Unit,
    onClickAllDownload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowMenu by remember { mutableStateOf(false) }
    val loadUrl = if (item.extension.lowercase() == "gif") item.originalUrl else item.thumbnailUrl

    SubcomposeAsyncImage(
        modifier = modifier
            .aspectRatio(item.aspectRatio)
            .combinedClickable(
                onClick = { onClickImage.invoke(item) },
                onLongClick = { isShowMenu = true },
            ),
        model = ImageRequest.Builder(LocalContext.current)
            .fanboxHeader()
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

    if (isShowMenu) {
        PostImageMenuDialog(
            onClickDownload = { onClickDownload.invoke() },
            onClickAllDownload = { onClickAllDownload.invoke() },
            onDismissRequest = { isShowMenu = false },
        )
    }
}
