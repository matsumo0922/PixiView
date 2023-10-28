package caios.android.pixiview.core.model.fanbox

import caios.android.pixiview.core.model.fanbox.entity.FanboxNewsLattersEntity
import kotlinx.serialization.SerialName

data class FanboxNewsLetter(
    val id: String,
    val body: String,
    val createdAt: String,
    val creator: FanboxCreator,
    val isRead: Boolean
)
