package caios.android.pixiview.feature.creator.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.ui.extensition.SimmerPlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import caios.android.pixiview.core.ui.extensition.marquee
import caios.android.pixiview.core.ui.theme.bold
import caios.android.pixiview.core.ui.theme.center
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
internal fun CreatorTopHeader(
    creatorDetail: FanboxCreatorDetail,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .blur(16.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .fanboxHeader()
                .crossfade(true)
                .data(creatorDetail.coverImageUrl)
                .build(),
            loading = {
                SimmerPlaceHolder()
            },
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .align(Alignment.TopCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.surface))),
        )

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .size(224.dp)
                .aspectRatio(1f),
            shape = RoundedCornerShape(8.dp),
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4 / 3f),
                model = ImageRequest.Builder(LocalContext.current)
                    .fanboxHeader()
                    .crossfade(true)
                    .data(creatorDetail.coverImageUrl)
                    .build(),
                loading = {
                    SimmerPlaceHolder()
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        }

        Text(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth()
                .marquee(),
            text = creatorDetail.user.name,
            style = MaterialTheme.typography.headlineSmall.center().bold(),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
