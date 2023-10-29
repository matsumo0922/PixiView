package caios.android.pixiview.core.model.fanbox

import java.time.OffsetDateTime

data class FanboxPaidRecord(
    val id: String,
    val paidAmount: Int,
    val paymentDatetime: OffsetDateTime,
    val paymentMethod: String,
    val creator: FanboxCreator,
)
