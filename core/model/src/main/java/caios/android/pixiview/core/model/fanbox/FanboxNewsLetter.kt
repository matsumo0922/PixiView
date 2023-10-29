package caios.android.pixiview.core.model.fanbox

import caios.android.pixiview.core.model.fanbox.id.NewsLetterId

data class FanboxNewsLetter(
    val id: NewsLetterId,
    val body: String,
    val createdAt: String,
    val creator: FanboxCreator,
    val isRead: Boolean,
)
