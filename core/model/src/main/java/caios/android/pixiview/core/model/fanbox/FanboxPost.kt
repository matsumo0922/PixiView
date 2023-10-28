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
) {
    companion object {
        fun dummy() = FanboxPost(
            id = "",
            title = "週末こっそり配信絵 Vol.159",
            excerpt = "~23:30 くらいまで、軽く配信します～！！",
            publishedDatetime = "2020/09/22",
            cover = FanboxCover(
                url = "https://downloads.fanbox.cc/images/post/6894357/w/1200/kcksgQDZpzodzjrvTrlJ834X.jpeg",
                type = "image/jpeg",
            ),
            isLiked = false,
            likeCount = 12,
            commentCount = 3,
            feeRequired = 500,
            isRestricted = false,
            hasAdultContent = false,
            tags = emptyList(),
            updatedDatetime = "2020/09/22",
            user = FanboxUser.dummy()
        )
    }
}
