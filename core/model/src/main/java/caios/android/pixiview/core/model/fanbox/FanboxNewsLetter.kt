package caios.android.pixiview.core.model.fanbox

import caios.android.pixiview.core.model.fanbox.id.NewsLetterId
import java.time.OffsetDateTime

data class FanboxNewsLetter(
    val id: NewsLetterId,
    val body: String,
    val createdAt: OffsetDateTime,
    val creator: FanboxCreator,
    val isRead: Boolean,
)
