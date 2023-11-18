package caios.android.pixiview.core.model.fanbox

import caios.android.pixiview.core.common.serializer.OffsetDateTimeSerializer
import caios.android.pixiview.core.model.fanbox.id.PostId
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class FanboxPost(
    val id: PostId,
    val title: String,
    val cover: FanboxCover?,
    val user: FanboxUser,
    val excerpt: String,
    val feeRequired: Int,
    val hasAdultContent: Boolean,
    var isLiked: Boolean,
    val isRestricted: Boolean,
    val likeCount: Int,
    val commentCount: Int,
    val tags: List<String>,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val publishedDatetime: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val updatedDatetime: OffsetDateTime,
) {
    companion object {
        fun dummy() = FanboxPost(
            id = PostId(""),
            title = "週末こっそり配信絵 Vol.159",
            excerpt = "~23:30 くらいまで、軽く配信します～！！",
            publishedDatetime = OffsetDateTime.MAX,
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
            updatedDatetime = OffsetDateTime.MAX,
            user = FanboxUser.dummy(),
        )
    }
}
