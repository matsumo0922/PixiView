package caios.android.pixiview.core.model.fanbox

import caios.android.pixiview.core.model.fanbox.id.PlanId

data class FanboxCreatorPlan(
    val id: PlanId,
    val title: String,
    val description: String,
    val fee: Int,
    val coverImageUrl: String?,
    val hasAdultContent: Boolean,
    val paymentMethod: String?,
    val user: FanboxUser,
)
