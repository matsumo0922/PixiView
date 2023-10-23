package caios.android.pixiview.core.model.fanbox

import kotlinx.serialization.SerialName

data class FanboxUser(
    val userId: String,
    val creatorId: String,
    val name: String,
    val iconUrl: String,
)
