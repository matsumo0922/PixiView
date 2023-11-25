package caios.android.pixiview.feature.creator.payment.items

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.model.fanbox.FanboxPaidRecord
import caios.android.pixiview.core.model.fanbox.PaymentMethod
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.ui.theme.bold
import caios.android.pixiview.feature.creator.payment.Payment
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun PaymentItem(
    payment: Payment,
    onClickCreator: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)),
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
        ) {
            TitleItem(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                dateTime = payment.paymentDateTime,
                totalPaidAmount = payment.paidRecords.sumOf { it.paidAmount },
            )

            Spacer(modifier = Modifier.height(8.dp))

            for (item in payment.paidRecords) {
                PaidItem(
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            start = 8.dp,
                            end = 16.dp,
                        )
                        .fillMaxWidth(),
                    paidRecord = item,
                    onClickCreator = onClickCreator,
                )
            }
        }
    }
}

@Composable
private fun TitleItem(
    dateTime: OffsetDateTime,
    totalPaidAmount: Int,
    modifier: Modifier = Modifier,
) {
    val yearPattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "yyyy")
    val yearFormatter = DateTimeFormatter.ofPattern(yearPattern).withLocale(Locale.getDefault())

    val dayPattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMdd")
    val dayFormatter = DateTimeFormatter.ofPattern(dayPattern).withLocale(Locale.getDefault())

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = dateTime.format(yearFormatter),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = dateTime.format(dayFormatter),
                style = MaterialTheme.typography.titleMedium.bold(),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Text(
            text = "￥$totalPaidAmount",
            style = MaterialTheme.typography.titleMedium.bold(),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun PaidItem(
    paidRecord: FanboxPaidRecord,
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
                .clickable { onClickCreator.invoke(paidRecord.creator.user.creatorId) }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                model = ImageRequest.Builder(LocalContext.current)
                    .error(caios.android.pixiview.core.ui.R.drawable.im_default_user)
                    .data(paidRecord.creator.user.iconUrl)
                    .build(),
                contentDescription = null,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = paidRecord.creator.user.name,
                    style = MaterialTheme.typography.bodyMedium.bold(),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = when (paidRecord.paymentMethod) {
                        PaymentMethod.CARD -> stringResource(caios.android.pixiview.core.ui.R.string.creator_supporting_payment_method_card)
                        PaymentMethod.PAYPAL -> stringResource(caios.android.pixiview.core.ui.R.string.creator_supporting_payment_method_paypal)
                        PaymentMethod.CVS -> stringResource(caios.android.pixiview.core.ui.R.string.creator_supporting_payment_method_cvs)
                        PaymentMethod.UNKNOWN -> stringResource(caios.android.pixiview.core.ui.R.string.creator_supporting_payment_method_unknown)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Text(
            modifier = Modifier.padding(6.dp, 4.dp),
            text = "￥${paidRecord.paidAmount}",
            style = MaterialTheme.typography.bodyMedium.bold(),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
