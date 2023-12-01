package caios.android.fanbox.core.model.fanbox

import caios.android.fanbox.core.model.fanbox.id.CreatorId

data class FanboxCreator(
    val creatorId: CreatorId?,
    val user: FanboxUser,
)
