package caios.android.pixiview.feature.creator.support.item

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlan
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.ui.R
import caios.android.pixiview.core.ui.extensition.SimmerPlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import caios.android.pixiview.core.ui.theme.bold
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
internal fun SupportingCreatorItem(
    supportingPlan: FanboxCreatorPlan,
    onClickPlanDetail: (Uri) -> Unit,
    onClickFanCard: (CreatorId) -> Unit,
    onClickCreatorPlans: (CreatorId) -> Unit,
    onClickCreatorPosts: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClickCreatorPosts.invoke(supportingPlan.user.creatorId) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)),
    ) {
        if (supportingPlan.coverImageUrl != null) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(4.dp)),
                model = ImageRequest.Builder(LocalContext.current)
                    .fanboxHeader()
                    .data(supportingPlan.coverImageUrl)
                    .build(),
                loading = {
                    SimmerPlaceHolder()
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.DarkGray)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = 16.dp,
                    alignment = Alignment.CenterVertically,
                ),
            ) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Default.BrokenImage,
                    tint = Color.White,
                    contentDescription = null,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            UserSection(
                modifier = Modifier.fillMaxWidth(),
                plan = supportingPlan,
                onClickCreator = onClickCreatorPlans,
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = supportingPlan.title,
                style = MaterialTheme.typography.titleMedium.bold(),
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = supportingPlan.description.trimEnd(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { onClickFanCard.invoke(supportingPlan.user.creatorId) },
                ) {
                    Text(text = stringResource(R.string.creator_supporting_fan_card))
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onClickPlanDetail.invoke(supportingPlan.supportingBrowserUri) },
                ) {
                    Text(text = stringResource(R.string.creator_supporting_plan_detail))
                }
            }
        }
    }
}

@Composable
private fun UserSection(
    plan: FanboxCreatorPlan,
    onClickCreator: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(4.dp))
                .clickable { onClickCreator.invoke(plan.user.creatorId) }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(50)),
                model = ImageRequest.Builder(LocalContext.current)
                    .error(R.drawable.im_default_user)
                    .data(plan.user.iconUrl)
                    .build(),
                contentDescription = null,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = plan.user.name,
                    style = MaterialTheme.typography.bodyMedium.bold(),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = when (plan.paymentMethod) {
                        FanboxCreatorPlan.PaymentMethod.CARD -> stringResource(R.string.creator_supporting_payment_method_card)
                        FanboxCreatorPlan.PaymentMethod.PAYPAL -> stringResource(R.string.creator_supporting_payment_method_paypal)
                        FanboxCreatorPlan.PaymentMethod.CVS -> stringResource(R.string.creator_supporting_payment_method_cvs)
                        FanboxCreatorPlan.PaymentMethod.UNKNOWN -> stringResource(R.string.creator_supporting_payment_method_unknown)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Card(
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Text(
                modifier = Modifier.padding(6.dp, 4.dp),
                text = "￥${plan.fee}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview
@Composable
private fun PostItemPreview1() {
    SupportingCreatorItem(
        supportingPlan = FanboxCreatorPlan.dummy(),
        onClickPlanDetail = {},
        onClickFanCard = {},
        onClickCreatorPlans = {},
        onClickCreatorPosts = {},
    )
}
