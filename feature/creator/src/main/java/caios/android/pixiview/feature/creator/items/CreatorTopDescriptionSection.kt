package caios.android.pixiview.feature.creator.items

import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.util.LinkifyCompat
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.ui.extensition.SimmerPlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
internal fun CreatorTopDescriptionSection(
    creatorDetail: FanboxCreatorDetail,
    modifier: Modifier = Modifier,
) {
    val descriptionFontSize = MaterialTheme.typography.bodyMedium.fontSize
    val descriptionFontColor = MaterialTheme.colorScheme.onSurface

    Card(
        modifier = modifier.padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(creatorDetail.profileItems) {
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(4.dp)),
                        model = ImageRequest.Builder(LocalContext.current)
                            .fanboxHeader()
                            .crossfade(true)
                            .data(it.thumbnailUrl)
                            .build(),
                        loading = {
                            SimmerPlaceHolder()
                        },
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                    )
                }
            }

            AndroidView(
                modifier = Modifier.padding(horizontal = 16.dp),
                factory = { context ->
                    TextView(context).apply {
                        movementMethod = LinkMovementMethod.getInstance()
                        text = creatorDetail.description
                        textSize = descriptionFontSize.value
                        setTextColor(descriptionFontColor.toArgb())
                    }.also {
                        LinkifyCompat.addLinks(it, Linkify.WEB_URLS)
                    }
                },
            )
        }
    }
}
