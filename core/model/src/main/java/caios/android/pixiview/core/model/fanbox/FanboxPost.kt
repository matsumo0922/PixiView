package caios.android.pixiview.core.model.fanbox

data class FanboxPost(
    val id: String,
    val title: String,
    val cover: FanboxCover?,
    val user: FanboxUser,
    val excerpt: String,
    val feeRequired: Int,
    val hasAdultContent: Boolean,
    val isLiked: Boolean,
    val isRestricted: Boolean,
    val likeCount: Int,
    val commentCount: Int,
    val tags: List<String>,
    val publishedDatetime: String,
    val updatedDatetime: String,
)
