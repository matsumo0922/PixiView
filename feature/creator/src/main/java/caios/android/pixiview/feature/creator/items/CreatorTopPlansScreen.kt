package caios.android.pixiview.feature.creator.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlan
import caios.android.pixiview.core.ui.extensition.SimmerPlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun CreatorTopPlansScreen(
    creatorDetail: FanboxCreatorDetail,
    creatorPlans: ImmutableList<FanboxCreatorPlan>,
    onClickPlan: (FanboxCreatorPlan) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (creatorDetail.profileItems.isNotEmpty()) {
            item {
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
            }
        }

        item {
            CreatorTopDescriptionSection(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                creatorDetail = creatorDetail,
            )
        }

        item {
            CreatorTopPlansSection(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                creatorPlans = creatorPlans,
                onClickPlan = onClickPlan,
            )
        }

        item {
            Spacer(modifier = Modifier.height(128.dp))
        }
    }
}
