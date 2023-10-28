package caios.android.pixiview.core.model.fanbox

import caios.android.pixiview.core.model.fanbox.entity.FanboxPaidRecordEntity
import kotlinx.serialization.SerialName

data class FanboxPaidRecord(
    val id: String,
    val paidAmount: Int,
    val paymentDatetime: String,
    val paymentMethod: String,
    val creator: FanboxCreator,
)
