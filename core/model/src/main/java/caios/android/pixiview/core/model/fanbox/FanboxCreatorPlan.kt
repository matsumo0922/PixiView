package caios.android.pixiview.core.model.fanbox

data class FanboxCreatorPlan(
    val id: String,
    val title: String,
    val description: String,
    val fee: Int,
    val coverImageUrl: String?,
    val hasAdultContent: Boolean,
    val paymentMethod: String?,
    val user: FanboxUser,
)
