package caios.android.pixiview.core.model.fanbox

data class FanboxNewsLetter(
    val id: String,
    val body: String,
    val createdAt: String,
    val creator: FanboxCreator,
    val isRead: Boolean,
)
