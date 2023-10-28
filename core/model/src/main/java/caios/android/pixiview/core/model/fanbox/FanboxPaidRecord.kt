package caios.android.pixiview.core.model.fanbox

data class FanboxPaidRecord(
    val id: String,
    val paidAmount: Int,
    val paymentDatetime: String,
    val paymentMethod: String,
    val creator: FanboxCreator,
)
