package caios.android.fanbox.core.model.fanbox

import java.time.OffsetDateTime

data class FanboxPaidRecord(
    val id: String,
    val paidAmount: Int,
    val paymentDateTime: OffsetDateTime,
    val paymentMethod: PaymentMethod,
    val creator: FanboxCreator,
)
