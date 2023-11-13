package caios.android.pixiview.feature.library.discovery.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.ui.extensition.SimmerPlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import caios.android.pixiview.core.ui.theme.bold
import caios.android.pixiview.feature.library.R
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
internal fun LibraryDiscoveryItem(
    creatorDetail: FanboxCreatorDetail,
    onClickCreator: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClickCreator.invoke(creatorDetail.creatorId) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)),
    ) {
        if (creatorDetail.profileItems.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .height(96.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(36.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .crossfade(true)
                    .error(R.drawable.im_default_user)
                    .data(creatorDetail.user.iconUrl)
                    .build(),
                loading = {
                    SimmerPlaceHolder()
                },
                contentDescription = null,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = creatorDetail.user.name,
                    style = MaterialTheme.typography.titleMedium.bold(),
                    color = MaterialTheme.colorScheme.primary,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = creatorDetail.description.replace("\n", " "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
