package caios.android.pixiview.core.model.fanbox

import caios.android.pixiview.core.model.fanbox.entity.FanboxPaidRecordEntity
import kotlinx.serialization.SerialName

data class FanboxPaidRecord(
    val id: String,
    val paidAmount: Int,
    val paymentDatetime: String,
    val paymentMethod: String,
    val creator: Creator,
) {
    data class Creator(
        val creatorId: String?,
        val isActive: Boolean,
        val user: FanboxUser,
    )
}
